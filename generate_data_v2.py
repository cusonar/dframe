import json
import random
import os

os.makedirs('src/main/resources/data', exist_ok=True)

# 1. Departments
departments = []
dept_id = 1

divisions = [
    ("경영지원본부", "Management Support Div"), 
    ("영업본부", "Sales Div"), 
    ("R&D센터", "R&D Center"), 
    ("IT본부", "IT Div"), 
    ("생산본부", "Production Div"), 
    ("품질경영본부", "Quality Management Div"), 
    ("마케팅본부", "Marketing Div"), 
    ("해외사업본부", "Overseas Business Div"), 
    ("재무본부", "Finance Div"), 
    ("전략기획실", "Strategic Planning Office")
]

teams = [
    ("기획팀", "Planning Team"), ("인사팀", "HR Team"), ("총무팀", "General Affairs Team"), 
    ("재무팀", "Finance Team"), ("회계팀", "Accounting Team"), ("영업1팀", "Sales Team 1"), 
    ("영업2팀", "Sales Team 2"), ("개발1팀", "Dev Team 1"), ("개발2팀", "Dev Team 2"), 
    ("품질관리팀", "QA Team"), ("마케팅기획팀", "Marketing Planning Team"), 
    ("해외영업1팀", "Overseas Sales Team 1"), ("생산1팀", "Production Team 1"), 
    ("법무팀", "Legal Team"), ("홍보팀", "PR Team")
]

root = {"id": dept_id, "name": "DOOSAN 그룹", "englishName": "DOOSAN Group", "parentId": None, "leaderUsername": "admin"}
departments.append(root)
root_id = dept_id
dept_id += 1

div_ids = []
for div_ko, div_en in divisions:
    d = {"id": dept_id, "name": div_ko, "englishName": div_en, "parentId": root_id, "leaderUsername": None}
    departments.append(d)
    div_ids.append(dept_id)
    dept_id += 1

while dept_id <= 800:
    for d_id in div_ids:
        if dept_id > 800: break
        t_ko, t_en = random.choice(teams)
        part_num = random.randint(1,9)
        d = {
            "id": dept_id, 
            "name": f"{t_ko} {part_num}파트", 
            "englishName": f"{t_en} Part {part_num}", 
            "parentId": d_id, 
            "leaderUsername": None
        }
        departments.append(d)
        dept_id += 1

# 2. Users
last_names_kr = ["김", "이", "박", "최", "정", "강", "조", "윤", "장", "임", "한", "오", "서", "신", "권", "황", "안", "송", "전", "홍"]
last_names_en = ["Kim", "Lee", "Park", "Choi", "Jung", "Kang", "Cho", "Yoon", "Jang", "Lim", "Han", "Oh", "Seo", "Shin", "Kwon", "Hwang", "Ahn", "Song", "Jeon", "Hong"]

first_names_kr = ["민준", "서준", "도윤", "시우", "주원", "하준", "지호", "지훈", "준우", "서연", "서윤", "지우", "서현", "하은", "하윤", "민서", "지유", "윤서", "지민", "수빈"]
first_names_en = ["Minjun", "Seojun", "Doyoon", "Siwoo", "Juwon", "Hajun", "Jiho", "Jihoon", "Junwoo", "Seoyeon", "Seoyoon", "Jiwoo", "Seohyun", "Haeun", "Hayoon", "Minseo", "Jiyoo", "Yoonseo", "Jimin", "Subin"]

job_titles = ["사원", "대리", "과장", "차장", "부장", "임원"]

users = []
users.append({
    "username": "admin",
    "name": "시스템 최고 관리자",
    "englishName": "System Admin",
    "password": "admin",
    "departmentId": root_id,
    "workingDepartmentId": root_id,
    "dispatchedDepartmentId": None,
    "jobTitle": "임원",
    "role": "ROLE_ADMIN"
})

for i in range(1, 5000):
    idx_l = random.randint(0, len(last_names_kr)-1)
    idx_f = random.randint(0, len(first_names_kr)-1)
    
    kr_name = f"{last_names_kr[idx_l]}{first_names_kr[idx_f]}"
    en_name = f"{first_names_en[idx_f]} {last_names_en[idx_l]}"
    
    dept_id = random.randint(1, 800)
    working_dept = dept_id if random.random() > 0.1 else random.randint(1, 800)
    dispatched_dept = random.randint(1, 800) if random.random() > 0.9 else None
    
    username = f"user{i}"
    users.append({
        "username": username,
        "name": kr_name,
        "englishName": en_name,
        "password": "user123",
        "departmentId": dept_id,
        "workingDepartmentId": working_dept,
        "dispatchedDepartmentId": dispatched_dept,
        "jobTitle": random.choice(job_titles),
        "role": "ROLE_MANAGER" if i % 100 == 0 else "ROLE_USER"
    })
    
    # Assign leader
    if i < len(departments):
        departments[i]["leaderUsername"] = username

with open('src/main/resources/data/departments.json', 'w', encoding='utf-8') as f:
    json.dump(departments, f, ensure_ascii=False, indent=2)

with open('src/main/resources/data/users.json', 'w', encoding='utf-8') as f:
    json.dump(users, f, ensure_ascii=False, indent=2)

print("Generated sample JSON data with extended fields.")
