# Alzheimer's Disease Prediction API

This API provides endpoints for predicting Alzheimer's disease risk based on various clinical and cognitive assessments.

## Setup

1. Create a virtual environment:
```bash
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate
```

2. Install dependencies:
```bash
pip install -r requirements.txt
```

3. Make sure your trained model files are in the root directory:
- `alzheimer_model.joblib`
- `scaler.joblib`

4. Run the API:
```bash
python app.py
```

The API will be available at `http://localhost:5000`

## API Endpoints

### Health Check
```
GET /health
```
Returns the API health status.

### Predict Alzheimer's Risk
```
POST /predict
```

Request body:
```json
{
    "age": 65,
    "gender": 0,  // 0 for Male, 1 for Female
    "education": 12,  // Years of education
    "socioEconomicStatus": 3,  // 1-5 scale
    "mmseScore": 25,  // Mini-Mental State Examination score (0-30)
    "cdScore": 1,  // Clinical Dementia Rating (0-3)
    "memoryScore": 75,  // Memory assessment score (0-100)
    "orientationScore": 80,  // Orientation test score (0-100)
    "visualScore": 70,  // Visual memory test score (0-100)
    "cognitiveScore": 65  // Cognitive assessment score (0-100)
}
```

Response:
```json
{
    "probability": 0.65,
    "prediction": 1,  // 0 for No Alzheimer's, 1 for Alzheimer's
    "riskLevel": "MODERATE",
    "recommendations": [
        "Schedule consultation with healthcare provider",
        "Increase frequency of cognitive exercises",
        "Monitor memory changes",
        "Consider lifestyle modifications",
        "Regular check-ups recommended"
    ]
}
```

## Error Handling

The API returns appropriate error messages for:
- Missing required fields (400 Bad Request)
- Invalid input values (400 Bad Request)
- Server errors (500 Internal Server Error)

## Deployment

For production deployment, use gunicorn:
```bash
gunicorn app:app
``` 