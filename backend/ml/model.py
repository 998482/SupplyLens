import joblib
import numpy as np
from pathlib import Path
from xgboost import XGBClassifier
from ml.dataset_gen import generate_dataset, WEATHER_MAP

MODEL_PATH = Path(__file__).parent / "model.pkl"

class SupplyLensModel:
    def __init__(self):
        df = generate_dataset()
        X = df[["distance_km", "weather_severity", "traffic_level"]].values
        y = df["has_disruption"].values

        self.clf = XGBClassifier(
            n_estimators=100,
            max_depth=4,
            learning_rate=0.1,
            eval_metric="logloss",
            random_state=42
        )
        self.clf.fit(X, y)
        joblib.dump(self.clf, MODEL_PATH)
        print("Model trained and saved.")

    def predict(self, origin, destination, distance_km, weather, traffic_level):
        weather_num = WEATHER_MAP.get(weather, 0)
        features = np.array([[distance_km, weather_num, traffic_level]])
        prob = float(self.clf.predict_proba(features)[0][1])
        risk_percent = round(prob * 100, 1)
        if risk_percent > 70:
            risk_level = "HIGH"
        elif risk_percent > 40:
            risk_level = "MEDIUM"
        else:
            risk_level = "LOW"
        confidence = round(abs(prob - 0.5) * 2, 2)
        return {
            "risk_percent": risk_percent,
            "risk_level": risk_level,
            "confidence": confidence
        }

model_instance = SupplyLensModel()

def load_model():
    return model_instance
