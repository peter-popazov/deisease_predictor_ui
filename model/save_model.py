import os

import joblib
import numpy as np
import pandas as pd
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import (
    accuracy_score, roc_auc_score
)
from sklearn.model_selection import train_test_split, StratifiedKFold, cross_val_score
from sklearn.preprocessing import StandardScaler
from constants import *


def prepare_data():
    df = pd.read_csv("data.csv")
    df = df.drop(['DoctorInCharge', 'PatientID', 'CholesterolTotal', 'CholesterolLDL', 'CholesterolHDL',
                  'CholesterolTriglycerides'], axis=1)

    num_columns = df.select_dtypes(include=['int64', 'float64']).columns
    num_columns_to_scale = [col for col in num_columns if col != TARGET_COL and col not in EXCLUDE_FROM_SCALING]

    scaler = StandardScaler()
    df[num_columns_to_scale] = scaler.fit_transform(df[num_columns_to_scale])

    return df, scaler, num_columns_to_scale


def train_model(df):
    np.random.seed(42)

    X = df.drop(TARGET_COL, axis=1)
    y = df[TARGET_COL]
    X_train_full, X_test, y_train_full, y_test = train_test_split(X, y, test_size=0.2, random_state=42, stratify=y)

    rf_model = RandomForestClassifier(
        n_estimators=100,
        max_depth=10,
        class_weight="balanced",
        random_state=42
    )

    cv = StratifiedKFold(n_splits=5, shuffle=True, random_state=42)
    cv_scores = cross_val_score(rf_model, X_train_full, y_train_full, cv=cv, scoring='roc_auc')

    rf_model.fit(X_train_full, y_train_full)

    y_pred_train = rf_model.predict(X_train_full)
    y_pred_test = rf_model.predict(X_test)
    y_pred_proba_test = rf_model.predict_proba(X_test)[:, 1]

    print("\nModel Performance:")
    print("Cross-validation ROC-AUC scores:", cv_scores)
    print("Mean CV ROC-AUC: {:.3f} (+/- {:.3f})".format(cv_scores.mean(), cv_scores.std() * 2))
    print("\nTraining Accuracy:", accuracy_score(y_train_full, y_pred_train))
    print("Test Accuracy:", accuracy_score(y_test, y_pred_test))
    print("\nTest ROC-AUC:", roc_auc_score(y_test, y_pred_proba_test))

    return rf_model, X_test, y_test


def save_model(model, scaler, num_columns_to_scale, X_test):
    os.makedirs('model_artifacts', exist_ok=True)

    joblib.dump(model, 'model_artifacts/alzheimer_model.joblib')

    joblib.dump(scaler, 'model_artifacts/scaler.joblib')

    all_columns = list(X_test.columns)
    categorical_columns = [col for col in all_columns if col not in num_columns_to_scale]

    column_info = {
        'numerical_columns': num_columns_to_scale,
        'categorical_columns': categorical_columns,
        'feature_names': all_columns,
        'feature_names_in_': all_columns
    }

    joblib.dump(column_info, 'model_artifacts/column_info.joblib')

    print("\nModel artifacts saved successfully!")
    print("Saved files:")
    print("- model_artifacts/alzheimer_model.joblib")
    print("- model_artifacts/scaler.joblib")
    print("- model_artifacts/column_info.joblib")
    print(f"Number of numerical columns: {len(num_columns_to_scale)}")
    print(f"Number of categorical columns: {len(categorical_columns)}")
    print(f"Total features: {len(all_columns)}")


if __name__ == "__main__":
    df, scaler, num_columns_to_scale = prepare_data()
    model, X_test, y_test = train_model(df)
    save_model(model, scaler, num_columns_to_scale, X_test)
    print("\nModel training and saving process completed!")
