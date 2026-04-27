import pandas as pd
import numpy as np
import random

random.seed(42)
np.random.seed(42)

WEATHER_MAP = {"Clear": 0, "Cloudy": 1, "Storm": 2, "Typhoon": 3}

ORIGINS = ["Shanghai", "Shenzhen", "Mumbai", "Ho Chi Minh City",
           "Chongqing", "Singapore", "Bangkok", "Jakarta", "Karachi", "Chennai"]

DESTINATIONS = ["Los Angeles", "Newark", "London", "Sydney",
                "Tokyo", "Rotterdam", "Dubai", "New York"]

def generate_dataset():
    rows = []
    for _ in range(600):
        origin = random.choice(ORIGINS)
        destination = random.choice(DESTINATIONS)
        distance_km = random.randint(500, 18000)
        weather_severity = np.random.choice([0,1,2,3], p=[0.40,0.30,0.20,0.10])
        traffic_level = random.randint(0, 10)

        disruption = 0
        if weather_severity >= 2 and traffic_level >= 7:
            disruption = 1
        elif distance_km > 12000 and weather_severity >= 1:
            disruption = 1
        elif traffic_level >= 9:
            disruption = 1

        if random.random() < 0.15:
            disruption = 1 - disruption

        rows.append({
            "origin": origin,
            "destination": destination,
            "distance_km": distance_km,
            "weather_severity": int(weather_severity),
            "traffic_level": traffic_level,
            "has_disruption": disruption
        })

    return pd.DataFrame(rows)
