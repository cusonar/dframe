import json
import random
import os

os.makedirs('src/main/resources/data', exist_ok=True)

# 1. Departments
departments = []
dept_id = 1

divisions = ["경영지원본부", "영업본부", "R&D센터", "IT본부", "생산본부", "품질경영본부", "마케팅본부", "해외사업본부", "재무본부", "전략기획실"]
teams = ["기획팀", "인사팀", "총무팀", "재무팀", "회계팀", "영업1팀", "영업2팀", "영업3팀", "개발1팀", "개발2팀", "품질관리팀", "마케팅기획팀", "온라인마케팅팀", "해외영업1팀", "해외영업2팀", "생산1팀", "생산2팀", "고객지원팀", "법무팀", "홍보팀"]

root = {"id": dept_id, "name": "DOOSAN 그룹", "parentId": None}
departments.append(root)
root_id = dept_id
dept_id += 1

div_ids = []
for div in divisions:
    d = {"id": dept_id, "name": div, "parentId": root_id}
    departments.append(d)
    div_ids.append(dept_id)
    dept_id += 1

while dept_id <= 800:
    for d_id in div_ids:
        if dept_id > 800: break
        d = {"id": dept_id, "name": f"{random.choice(teams)} {random.randint(1,9)}파트", "parentId": d_id}
        departments.append(d)
        dept_id += 1

with open('src/main/resources/data/departments.json', 'w', encoding='utf-8') as f:
    json.dump(departments, f, ensure_ascii=False, indent=2)

# 2. Users
last_names = ["김", "이", "박", "최", "정", "강", "조", "윤", "장", "임", "한", "오", "서", "신", "권", "황", "안", "송", "전", "홍", "유", "고", "문"]
first_names = ["민준", "서준", "예준", "도윤", "시우", "주원", "하준", "지호", "지훈", "준우", "서연", "서윤", "지우", "서현", "하은", "하윤", "민서", "지유", "윤서", "지민", "수빈", "지원", "은지", "수진", "현진", "영호", "철수", "민지", "영희", "동석", "태형", "정국", "지석", "재석", "명수", "하하", "길"]

users = []
users.append({
    "username": "admin",
    "name": "시스템 최고 관리자",
    "password": "admin",
    "departmentId": root_id,
    "role": "ROLE_ADMIN"
})

for i in range(1, 5000):
    users.append({
        "username": f"user{i}",
        "name": f"{random.choice(last_names)}{random.choice(first_names)}",
        "password": "user123",
        "departmentId": random.randint(1, 800),
        "role": "ROLE_MANAGER" if i % 100 == 0 else "ROLE_USER"
    })

with open('src/main/resources/data/users.json', 'w', encoding='utf-8') as f:
    json.dump(users, f, ensure_ascii=False, indent=2)

# 3. Menus
menus = [
    {"code": "USER", "title": "사용자 관리", "url": "/admin/users", "icon": "fas fa-users", "sortOrder": 1},
    {"code": "DEPT", "title": "부서 관리", "url": "/admin/departments", "icon": "fas fa-sitemap", "sortOrder": 2},
    {"code": "ROLE", "title": "권한 관리", "url": "/admin/roles", "icon": "fas fa-user-shield", "sortOrder": 3},
    {"code": "SYS", "title": "시스템 설정", "url": "/admin/settings", "icon": "fas fa-cogs", "sortOrder": 4},
    {"code": "AUDIT", "title": "감사 로그", "url": "/admin/audit", "icon": "fas fa-list", "sortOrder": 5}
]

with open('src/main/resources/data/menus.json', 'w', encoding='utf-8') as f:
    json.dump(menus, f, ensure_ascii=False, indent=2)

print("Generated sample JSON data files successfully.")
