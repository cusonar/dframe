import os

def replace_in_file(path, old_text, new_text):
    if not os.path.exists(path): return
    with open(path, 'r', encoding='utf-8') as f:
        content = f.read()
    content = content.replace(old_text, new_text)
    with open(path, 'w', encoding='utf-8') as f:
        f.write(content)

# 1. Rename files
if os.path.exists('src/main/java/com/doosan/dframe/domain/User.java'):
    os.rename('src/main/java/com/doosan/dframe/domain/User.java', 'src/main/java/com/doosan/dframe/domain/Employee.java')
if os.path.exists('src/main/java/com/doosan/dframe/repository/UserRepository.java'):
    os.rename('src/main/java/com/doosan/dframe/repository/UserRepository.java', 'src/main/java/com/doosan/dframe/repository/EmployeeRepository.java')
if os.path.exists('src/main/java/com/doosan/dframe/service/UserService.java'):
    os.rename('src/main/java/com/doosan/dframe/service/UserService.java', 'src/main/java/com/doosan/dframe/service/EmployeeService.java')
if os.path.exists('src/main/java/com/doosan/dframe/controller/AdminUserController.java'):
    os.rename('src/main/java/com/doosan/dframe/controller/AdminUserController.java', 'src/main/java/com/doosan/dframe/controller/AdminEmployeeController.java')

os.makedirs('src/main/resources/templates/admin/employees', exist_ok=True)
if os.path.exists('src/main/resources/templates/admin/users/list.html'):
    os.rename('src/main/resources/templates/admin/users/list.html', 'src/main/resources/templates/admin/employees/list.html')
    os.system('rm -rf src/main/resources/templates/admin/users')

if os.path.exists('src/main/resources/data/users.json'):
    os.rename('src/main/resources/data/users.json', 'src/main/resources/data/employees.json')

# 2. Modify Entity Class Names & Table Names
# Employee.java
f = 'src/main/java/com/doosan/dframe/domain/Employee.java'
replace_in_file(f, 'public class User', 'public class Employee')
replace_in_file(f, '@Table(name = "users")', '@Table(name = "employee")')
replace_in_file(f, 'name = "user_roles"', 'name = "employee_role"')
replace_in_file(f, 'name = "user_id"', 'name = "employee_id"')

# Department.java
f = 'src/main/java/com/doosan/dframe/domain/Department.java'
replace_in_file(f, '@Table(name = "departments")', '@Table(name = "department")')
replace_in_file(f, 'private User leader;', 'private Employee leader;')

# Role.java
f = 'src/main/java/com/doosan/dframe/domain/Role.java'
replace_in_file(f, '@Table(name = "roles")', '@Table(name = "role")')
replace_in_file(f, 'name = "role_authorities"', 'name = "role_authority"')

# Menu.java
f = 'src/main/java/com/doosan/dframe/domain/Menu.java'
replace_in_file(f, '@Table(name = "menus")', '@Table(name = "menu")')

# Authority.java
f = 'src/main/java/com/doosan/dframe/domain/Authority.java'
replace_in_file(f, '@Table(name = "authorities")', '@Table(name = "authority")')

# 3. EmployeeRepository.java
f = 'src/main/java/com/doosan/dframe/repository/EmployeeRepository.java'
replace_in_file(f, 'UserRepository', 'EmployeeRepository')
replace_in_file(f, 'User,', 'Employee,')
replace_in_file(f, 'Optional<User>', 'Optional<Employee>')
replace_in_file(f, 'FROM User u', 'FROM Employee u')
replace_in_file(f, 'Page<User>', 'Page<Employee>')

# 4. EmployeeService.java
f = 'src/main/java/com/doosan/dframe/service/EmployeeService.java'
replace_in_file(f, 'UserService', 'EmployeeService')
replace_in_file(f, 'UserRepository', 'EmployeeRepository')
replace_in_file(f, 'userRepository', 'employeeRepository')
replace_in_file(f, 'User user = new User()', 'Employee user = new Employee()')
replace_in_file(f, 'import com.doosan.dframe.domain.User;', 'import com.doosan.dframe.admin.employee.Employee;')

# 5. AdminEmployeeController.java
f = 'src/main/java/com/doosan/dframe/controller/AdminEmployeeController.java'
replace_in_file(f, 'AdminUserController', 'AdminEmployeeController')
replace_in_file(f, '/admin/users', '/admin/employees')
replace_in_file(f, 'admin/users/list', 'admin/employees/list')
replace_in_file(f, 'UserService', 'EmployeeService')
replace_in_file(f, 'userService', 'employeeService')
replace_in_file(f, 'UserRepository', 'EmployeeRepository')
replace_in_file(f, 'userRepository', 'employeeRepository')
replace_in_file(f, 'Page<com.doosan.dframe.domain.User>', 'Page<com.doosan.dframe.admin.employee.Employee>')
replace_in_file(f, 'createUser(', 'createEmployee(')

# 6. CustomUserDetails.java
f = 'src/main/java/com/doosan/dframe/security/CustomUserDetails.java'
replace_in_file(f, 'import com.doosan.dframe.domain.User;', 'import com.doosan.dframe.admin.employee.Employee;')
replace_in_file(f, 'private User user;', 'private Employee user;')
replace_in_file(f, 'public CustomUserDetails(User user)', 'public CustomUserDetails(Employee user)')
replace_in_file(f, 'public User getUser()', 'public Employee getEmployee()')

# 7. CustomUserDetailsService.java
f = 'src/main/java/com/doosan/dframe/security/CustomUserDetailsService.java'
replace_in_file(f, 'UserRepository', 'EmployeeRepository')
replace_in_file(f, 'userRepository', 'employeeRepository')
replace_in_file(f, 'User user =', 'Employee user =')
replace_in_file(f, 'import com.doosan.dframe.domain.User;', 'import com.doosan.dframe.admin.employee.Employee;')

# 8. DataLoader.java
f = 'src/main/java/com/doosan/dframe/config/DataLoader.java'
replace_in_file(f, 'UserRepository', 'EmployeeRepository')
replace_in_file(f, 'userRepository', 'employeeRepository')
replace_in_file(f, 'List<User>', 'List<Employee>')
replace_in_file(f, 'User u = new User()', 'Employee u = new Employee()')
replace_in_file(f, 'User admin = new User()', 'Employee admin = new Employee()')
replace_in_file(f, 'Map<String, User>', 'Map<String, Employee>')
replace_in_file(f, 'User leader =', 'Employee leader =')
replace_in_file(f, 'UserDto', 'EmployeeDto')
replace_in_file(f, 'data/users.json', 'data/employees.json')

# 9. Sidebar and menus.json
sidebar_path = 'src/main/resources/templates/fragments/sidebar.html'
replace_in_file(sidebar_path, '/admin/users', '/admin/employees')
replace_in_file(sidebar_path, '사용자 관리', '직원 관리')

menus_path = 'src/main/resources/data/menus.json'
replace_in_file(menus_path, '/admin/users', '/admin/employees')
replace_in_file(menus_path, '사용자 관리', '직원 관리')

# 10. list.html (Employees)
list_path = 'src/main/resources/templates/admin/employees/list.html'
replace_in_file(list_path, '/admin/users', '/admin/employees')
replace_in_file(list_path, 'createUserModal', 'createEmployeeModal')
replace_in_file(list_path, 'create user', 'create employee')
replace_in_file(list_path, '사용자 관리', '직원 관리')
replace_in_file(list_path, '사용자가', '직원이')

print("Refactoring complete.")
