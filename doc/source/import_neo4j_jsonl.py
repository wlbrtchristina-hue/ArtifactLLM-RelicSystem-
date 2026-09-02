import argparse
import json
from neo4j import GraphDatabase

def make_query():
    return """
UNWIND $rows AS row
MERGE (a:Artifact {uuid: row.uuid})
SET a.name = row.name,
    a.culturalNo = row.cultural_no,
    a.detailUrl = row.detail_url,
    a.hasImage = coalesce(row.has_image,false)
WITH a, row
FOREACH (_ IN CASE WHEN row.category IS NOT NULL AND row.category <> '' THEN [1] ELSE [] END |
  MERGE (c:Category {name: row.category})
  MERGE (a)-[:BELONGS_TO]->(c)
)
FOREACH (_ IN CASE WHEN row.dynasty IS NOT NULL AND row.dynasty <> '' THEN [1] ELSE [] END |
  MERGE (e:Era {name: row.dynasty})
  MERGE (a)-[:FROM_ERA]->(e)
)
FOREACH (_ IN CASE WHEN row.has_image AND row.center_image IS NOT NULL THEN [1] ELSE [] END |
  MERGE (i:Image {url: row.center_image})
  SET i.width = toFloat(row.image_width), i.height = toFloat(row.image_height)
  MERGE (a)-[:HAS_IMAGE]->(i)
)"""

def ensure_constraints(tx):
    tx.run("CREATE CONSTRAINT IF NOT EXISTS FOR (a:Artifact) REQUIRE a.uuid IS UNIQUE")
    tx.run("CREATE CONSTRAINT IF NOT EXISTS FOR (c:Category) REQUIRE c.name IS UNIQUE")
    tx.run("CREATE CONSTRAINT IF NOT EXISTS FOR (e:Era) REQUIRE e.name IS UNIQUE")
    tx.run("CREATE CONSTRAINT IF NOT EXISTS FOR (i:Image) REQUIRE i.url IS UNIQUE")

def load_jsonl(path, chunk_size=1000):
    buf = []
    with open(path, 'r', encoding='utf-8') as f:
        for line in f:
            if not line.strip():
                continue
            row = json.loads(line)
            buf.append(row)
            if len(buf) >= chunk_size:
                yield buf
                buf = []
    if buf:
        yield buf

def main(uri, user, password, input_path, chunk_size):
    driver = GraphDatabase.driver(uri, auth=(user, password))
    query = make_query()
    total = 0
    with driver.session() as session:
        session.execute_write(lambda tx: ensure_constraints(tx))
        for chunk in load_jsonl(input_path, chunk_size):
            session.execute_write(lambda tx: tx.run(query, rows=chunk))
            total += len(chunk)
            print(f"imported={total}")
    driver.close()

if __name__ == "__main__":
    p = argparse.ArgumentParser()
    p.add_argument("--uri", default="bolt://localhost:7687")
    p.add_argument("--user", default="neo4j")
    p.add_argument("--password", required=True)
    p.add_argument("--input", required=True)
    p.add_argument("--chunk-size", type=int, default=1000)
    args = p.parse_args()
    main(args.uri, args.user, args.password, args.input, args.chunk_size)
