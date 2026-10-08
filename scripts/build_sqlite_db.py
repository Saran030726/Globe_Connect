#!/usr/bin/env python3
import json
import sqlite3
import os

assets_dir = "/app/applet/app/src/main/assets"
db_path = f"{assets_dir}/globe_connect.db"

if os.path.exists(db_path):
    os.remove(db_path)

conn = sqlite3.connect(db_path)
cur = conn.cursor()

# Create states table
cur.execute('''
CREATE TABLE IF NOT EXISTS states (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL,
    slug TEXT NOT NULL,
    code TEXT NOT NULL,
    capital TEXT NOT NULL,
    best_season TEXT NOT NULL,
    description TEXT NOT NULL,
    total_spots INTEGER NOT NULL,
    total_hidden INTEGER NOT NULL
)
''')

# Create places table
cur.execute('''
CREATE TABLE IF NOT EXISTS places (
    id INTEGER PRIMARY KEY,
    state_id INTEGER NOT NULL,
    state_name TEXT NOT NULL,
    district_id INTEGER,
    city_id INTEGER,
    name TEXT NOT NULL,
    slug TEXT NOT NULL,
    description TEXT,
    category TEXT NOT NULL,
    image_url TEXT,
    latitude REAL,
    longitude REAL,
    best_season TEXT,
    duration TEXT,
    entry_fee TEXT,
    vr_available INTEGER NOT NULL,
    is_hidden INTEGER NOT NULL,
    code_in_pdf TEXT NOT NULL,
    source TEXT NOT NULL,
    associated_tourist_spot_id INTEGER,
    FOREIGN KEY (state_id) REFERENCES states (id)
)
''')

# Create index for ultra-fast search
cur.execute('CREATE INDEX idx_places_name ON places (name)')
cur.execute('CREATE INDEX idx_places_state ON places (state_id)')
cur.execute('CREATE INDEX idx_places_hidden ON places (is_hidden)')
cur.execute('CREATE INDEX idx_places_category ON places (category)')

# Create vr_experiences table
cur.execute('''
CREATE TABLE IF NOT EXISTS vr_experiences (
    id INTEGER PRIMARY KEY,
    place_id INTEGER NOT NULL,
    spot_name TEXT NOT NULL,
    title TEXT NOT NULL,
    provider TEXT NOT NULL,
    panorama_id TEXT NOT NULL,
    latitude REAL NOT NULL,
    longitude REAL NOT NULL,
    available INTEGER NOT NULL,
    source_url TEXT NOT NULL
)
''')

# Load and insert states
with open(f"{assets_dir}/tourism_states.json") as f:
    states = json.load(f)

for s in states:
    cur.execute('''
    INSERT INTO states (id, name, slug, code, capital, best_season, description, total_spots, total_hidden)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
    ''', (s["id"], s["name"], s["slug"], s["code"], s["capital"], s["best_season"], s["description"], s["total_spots"], s["total_hidden"]))

# Load and insert places
with open(f"{assets_dir}/tourism_places.json") as f:
    places = json.load(f)

for p in places:
    cur.execute('''
    INSERT INTO places (id, state_id, state_name, district_id, city_id, name, slug, description, category, image_url, latitude, longitude, best_season, duration, entry_fee, vr_available, is_hidden, code_in_pdf, source, associated_tourist_spot_id)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    ''', (p["id"], p["state_id"], p["state_name"], p["district_id"], p["city_id"], p["name"], p["slug"], p["description"], p["category"], p["image_url"], p["latitude"], p["longitude"], p["best_season"], p["duration"], p["entry_fee"], 1 if p["vr_available"] else 0, 1 if p["is_hidden"] else 0, p["code_in_pdf"], p["source"], p["associated_tourist_spot_id"]))

# Load and insert VR experiences
with open(f"{assets_dir}/tourism_experiences.json") as f:
    vrs = json.load(f)

for v in vrs:
    cur.execute('''
    INSERT INTO vr_experiences (id, place_id, spot_name, title, provider, panorama_id, latitude, longitude, available, source_url)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    ''', (v["id"], v["place_id"], v["spot_name"], v["title"], v["provider"], v["panorama_id"], v["latitude"], v["longitude"], 1 if v["available"] else 0, v["source_url"]))

conn.commit()
conn.close()
print(f"Successfully created SQLite database at {db_path} with {len(states)} states, {len(places)} places, {len(vrs)} VR experiences.")
