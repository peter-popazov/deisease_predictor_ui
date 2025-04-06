import joblib
import pandas as pd
import numpy as np
from flask import Flask, request, jsonify
from data_converter import AlzheimerDataConverter

app = Flask(__name__)

# Load all model artifacts
model_path = 'model_artifacts/alzheimer_model.joblib'
scaler_path = 'model_artifacts/scaler.joblib'
column_info_path = 'model_artifacts/column_info.joblib'

try:
    model = joblib.load(model_path)
    scaler = joblib.load(scaler_path)
    column_info = joblib.load(column_info_path)
    print("All model artifacts loaded successfully!")
except FileNotFoundError as e:
    raise FileNotFoundError(f"Model artifact not found: {str(e)}")

# Initialize the data converter
converter = AlzheimerDataConverter()


@app.route('/predict', methods=['POST'])
def predict():
    try:
        data = request.get_json()

        # First, convert the input data using the converter
        try:
            converted_data = {}
            for feature, value in data.items():
                converted_data[feature] = converter.convert_single_value(feature, value)
        except ValueError as e:
            return jsonify({"error": str(e)}), 400

        # Get the exact features and their order from the model
        if hasattr(model, 'feature_names_in_'):
            model_features = model.feature_names_in_
        else:
            # If not available, try to get from column_info
            model_features = column_info.get('feature_names', converter.get_all_features())

        # Create DataFrame with exact same column order as model training
        input_df = pd.DataFrame(columns=model_features)
        input_df.loc[0] = np.nan  # Initialize with NaN

        # Fill in the provided values
        for feature in model_features:
            if feature in converted_data:
                input_df.at[0, feature] = converted_data[feature]

        # Check for missing required features
        missing_features = [col for col in model_features if pd.isna(input_df[col]).any()]
        if missing_features:
            return jsonify({
                "error": "Missing required features",
                "missing_features": missing_features,
                "message": "Please provide values for all required features"
            }), 400

        # Separate numerical features for scaling
        if 'numerical_columns' in column_info:
            numerical_features = [col for col in column_info['numerical_columns'] if col in model_features]
            if numerical_features:
                input_df[numerical_features] = scaler.transform(input_df[numerical_features].values)

        # Debug information
        print("Input DataFrame shape:", input_df.shape)
        print("Expected features:", len(model_features))
        print("First few columns:", list(input_df.columns[:5]))

        # Make prediction - ensure we don't lose column order
        prediction_proba = model.predict_proba(input_df[model_features])[0]
        prediction = model.predict(input_df[model_features])[0]

        # Generate risk level and recommendations
        risk_probability = float(prediction_proba[1])  # Probability of having Alzheimer's

        if risk_probability >= 0.7:
            risk_level = "HIGH"
            recommendations = [
                "Immediate consultation with a neurologist is strongly recommended",
                "Complete neurological evaluation should be scheduled",
                "Consider medication evaluation",
                "Establish a strong support system with family members",
                "Regular monitoring of symptoms is essential"
            ]
        elif risk_probability >= 0.3:
            risk_level = "MODERATE"
            recommendations = [
                "Schedule consultation with healthcare provider",
                "Increase frequency of cognitive exercises",
                "Monitor memory changes",
                "Consider lifestyle modifications",
                "Regular check-ups recommended"
            ]
        else:
            risk_level = "LOW"
            recommendations = [
                "Continue regular cognitive exercises",
                "Maintain healthy lifestyle habits",
                "Annual check-ups with healthcare provider",
                "Stay socially active",
                "Monitor any changes in memory or cognitive function"
            ]

        # Prepare response
        response = {
            "probability": risk_probability,
            "prediction": int(prediction),
            "riskLevel": risk_level,
            "recommendations": recommendations
        }

        return jsonify(response)

    except KeyError as e:
        return jsonify({"error": f"Missing required field: {str(e)}"}), 400
    except Exception as e:
        return jsonify({"error": f"An error occurred: {str(e)}"}), 500

@app.route('/valid_values', methods=['GET'])
def get_valid_values():
    """Endpoint to get valid values for all features"""
    feature_info = {}

    # Get all categorical mappings
    for feature, mapping in converter.categorical_mappings.items():
        feature_info[feature] = mapping

    # Get typical ranges for numerical features
    for feature in converter.numerical_features:
        feature_info[feature] = converter.get_valid_values(feature)

    return jsonify(feature_info)

@app.route('/features', methods=['GET'])
def get_features():
    """Endpoint to get all available features"""
    features = converter.get_all_features()
    return jsonify({"features": features})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)