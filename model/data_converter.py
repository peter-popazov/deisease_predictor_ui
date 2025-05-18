import pandas as pd


class AlzheimerDataConverter:
    """
    A class to convert categorical features in Alzheimer's dataset to their numeric representations
    """

    def __init__(self):
        self.categorical_mappings = {
            'Smoking': {'No': 0, 'Yes': 1},
            'FamilyHistoryAlzheimers': {'No': 0, 'Yes': 1},
            'CardiovascularDisease': {'No': 0, 'Yes': 1},
            'Diabetes': {'No': 0, 'Yes': 1},
            'Depression': {'No': 0, 'Yes': 1},
            'HeadInjury': {'No': 0, 'Yes': 1},
            'Hypertension': {'No': 0, 'Yes': 1},
            'MemoryComplaints': {'No': 0, 'Yes': 1},
            'BehavioralProblems': {'No': 0, 'Yes': 1},
            'Confusion': {'No': 0, 'Yes': 1},
            'Disorientation': {'No': 0, 'Yes': 1},
            'PersonalityChanges': {'No': 0, 'Yes': 1},
            'DifficultyCompletingTasks': {'No': 0, 'Yes': 1},
            'Forgetfulness': {'No': 0, 'Yes': 1},

            'Gender': {'Male': 0, 'Female': 1},

            'Ethnicity': {'Caucasian': 0, 'African American': 1, 'Asian': 2, 'Other': 3},

            'EducationLevel': {'None': 0, 'High School': 1, 'Bachelor\'s': 2, 'Higher': 3}
        }

        self.numerical_features = [
            'Age', 'BMI', 'AlcoholConsumption', 'PhysicalActivity', 'DietQuality',
            'SleepQuality', 'SystolicBP', 'DiastolicBP',
            'MMSE', 'FunctionalAssessment', 'ADL'
        ]

    def convert_single_value(self, feature_name, value):
        """
        Convert a single value for a specified feature

        Args:
            feature_name (str): Name of the feature
            value: The value to convert

        Returns:
            The converted value
        """
        if feature_name in self.categorical_mappings:
            try:
                return self.categorical_mappings[feature_name][value]
            except KeyError:
                valid_values = list(self.categorical_mappings[feature_name].keys())
                raise ValueError(
                    f"Invalid value '{value}' for feature '{feature_name}'. Valid values are: {valid_values}")
        else:
            # For numerical features, try to convert to appropriate type
            try:
                return float(value)
            except ValueError:
                raise ValueError(f"Expected numerical value for '{feature_name}', got '{value}'")

    def convert_data(self, data):
        """
        Convert all data in a dictionary or DataFrame

        Args:
            data: Dictionary or DataFrame with feature values

        Returns:
            Dictionary or DataFrame with converted values
        """
        if isinstance(data, dict):
            converted_data = {}
            for feature, value in data.items():
                converted_data[feature] = self.convert_single_value(feature, value)
            return converted_data

        elif isinstance(data, pd.DataFrame):
            converted_df = data.copy()
            for feature in data.columns:
                if feature in self.categorical_mappings:
                    converted_df[feature] = data[feature].map(self.categorical_mappings[feature])
                    # Handle missing mappings
                    missing_values = data[feature][~data[feature].isin(self.categorical_mappings[feature].keys())]
                    if not missing_values.empty:
                        raise ValueError(f"Invalid values for '{feature}': {list(missing_values.unique())}. "
                                         f"Valid values are: {list(self.categorical_mappings[feature].keys())}")
            return converted_df

        else:
            raise TypeError("Input must be a dictionary or pandas DataFrame")

    def get_valid_values(self, feature_name):
        """
        Get valid values for a specific feature

        Args:
            feature_name (str): Name of the feature

        Returns:
            Dictionary of valid values or range for numerical features
        """
        if feature_name in self.categorical_mappings:
            return self.categorical_mappings[feature_name]
        elif feature_name in self.numerical_features:
            # Return typical ranges for numerical features
            ranges = {
                'Age': '60-90 years',
                'BMI': '15-40',
                'AlcoholConsumption': '0-20 units/week',
                'PhysicalActivity': '0-10 hours/week',
                'DietQuality': '0-10 score',
                'SleepQuality': '4-10 score',
                'SystolicBP': '90-180 mmHg',
                'DiastolicBP': '60-120 mmHg',
                'MMSE': '0-30 score',
                # 'FunctionalAssessment': '0-10 score',
                # 'ADL': '0-10 score'
            }
            return ranges.get(feature_name, 'Numerical value')
        else:
            return "Unknown feature"

    def get_all_features(self):
        """Return a list of all features handled by this converter"""
        return sorted(list(self.categorical_mappings.keys()) + self.numerical_features)
