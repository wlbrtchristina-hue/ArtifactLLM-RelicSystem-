import json
import os

def escape_sql(value):
    if value is None:
        return "NULL"
    return "'" + str(value).replace("'", "''").replace("\\", "\\\\") + "'"

def generate_sql():
    jsonl_path = r'c:\Users\user\Desktop\2班_老师指定的组队_大模型增强的文物资源知识管理系统\doc\source\raw_artifacts.jsonl'
    output_path = r'c:\Users\user\Desktop\2班_老师指定的组队_大模型增强的文物资源知识管理系统\doc\source\import_data.sql'
    
    if not os.path.exists(jsonl_path):
        print(f"Error: File not found at {jsonl_path}")
        return

    print(f"Reading from {jsonl_path}...")
    
    type_map = {}
    type_id_counter = 1
    relic_id_counter = 1
    
    # We will buffer statements to write in chunks or just write line by line.
    # Writing line by line is safer for memory.
    
    with open(output_path, 'w', encoding='utf-8') as out_f:
        out_f.write("-- Generated import script\n")
        out_f.write("SET NAMES utf8mb4;\n")
        out_f.write("SET FOREIGN_KEY_CHECKS = 0;\n\n")
        
        # We need to handle categories first if we want to be clean, 
        # but since we are assigning IDs explicitly, we can insert them as we find them 
        # OR we can collect them all first.
        # Let's collect them all first to keep the SQL clean (types first, then relics).
        # However, reading the file twice might be slow.
        # But we can just emit the INSERT for type whenever we encounter a new one.
        # Since we are using explicit IDs, order doesn't strictly matter for FKs if we disable checks,
        # but it's better to have types defined before usage if checks were on.
        # Given the "SET FOREIGN_KEY_CHECKS = 0", we can interleave.
        # BUT, for readability, let's just interleave or emit as we go.
        # Actually, let's keep types at the top. So we need two passes or store data in memory.
        # 100k records is not that big for memory (maybe 100-200MB). Let's try to load all distinct categories first.
        
        # Pass 1: Categories
        print("Scanning for categories...")
        with open(jsonl_path, 'r', encoding='utf-8') as in_f:
            for line in in_f:
                try:
                    data = json.loads(line)
                    category = data.get('category')
                    if category and category not in type_map:
                        type_map[category] = type_id_counter
                        type_id_counter += 1
                except json.JSONDecodeError:
                    continue
        
        print(f"Found {len(type_map)} categories.")
        
        # Write Type Inserts
        out_f.write("-- Relic Types\n")
        for category, type_id in type_map.items():
            sql = f"INSERT INTO relics_type (relics_type_id, type_name, created_by) VALUES ({type_id}, {escape_sql(category)}, 1);\n"
            out_f.write(sql)
        out_f.write("\n")
        
        # Pass 2: Relics
        print("Generating relic inserts...")
        out_f.write("-- Cultural Relics and Resources\n")
        
        with open(jsonl_path, 'r', encoding='utf-8') as in_f:
            for line in in_f:
                try:
                    data = json.loads(line)
                    
                    # Extract fields
                    name = data.get('name')
                    era = data.get('dynasty', 'Unknown')
                    category = data.get('category')
                    cultural_code = data.get('cultural_no')
                    external_id = data.get('uuid')
                    detail_url = data.get('detail_url')
                    center_image = data.get('center_image')
                    has_image = data.get('has_image')
                    
                    type_id = type_map.get(category)
                    if type_id is None:
                         # Should not happen if file hasn't changed between passes
                         # Fallback or skip
                         type_id = 0 # Or some default
                    
                    # Insert Relic
                    # Columns: relics_id, relics_name, era, relics_type_id, created_by, cultural_code, external_id, detail_url
                    val_name = escape_sql(name)
                    val_era = escape_sql(era)
                    val_code = escape_sql(cultural_code)
                    val_ext_id = escape_sql(external_id)
                    val_url = escape_sql(detail_url)
                    
                    sql = f"INSERT INTO cultural_relics (relics_id, relics_name, era, relics_type_id, created_by, cultural_code, external_id, detail_url) VALUES ({relic_id_counter}, {val_name}, {val_era}, {type_id}, 1, {val_code}, {val_ext_id}, {val_url});\n"
                    out_f.write(sql)
                    
                    # Insert Image if exists
                    if has_image and center_image:
                        val_content = escape_sql(center_image)
                        sql_res = f"INSERT INTO relic_multi_mode (relics_id, resource_type, resource_content, created_by) VALUES ({relic_id_counter}, 'image', {val_content}, 1);\n"
                        out_f.write(sql_res)
                    
                    relic_id_counter += 1
                    
                except json.JSONDecodeError:
                    continue
                    
        out_f.write("SET FOREIGN_KEY_CHECKS = 1;\n")
        
    print(f"Done. Generated SQL for {relic_id_counter - 1} relics.")

if __name__ == "__main__":
    generate_sql()
