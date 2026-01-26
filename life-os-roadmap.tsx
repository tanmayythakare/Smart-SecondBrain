import React, { useState } from 'react';
import { CheckCircle, Circle, Clock, Calendar, FileText, Terminal, Database, Shield, Layout, Link, BarChart, Rocket, TestTube, Book, Cloud } from 'lucide-react';

const LifeOSRoadmap = () => {
  const [selectedWeek, setSelectedWeek] = useState(1);
  const [completedTasks, setCompletedTasks] = useState({});

  const toggleTask = (dayId, taskId) => {
    setCompletedTasks(prev => ({
      ...prev,
      [`${dayId}-${taskId}`]: !prev[`${dayId}-${taskId}`]
    }));
  };

  const weeks = [
    {
      id: 1,
      title: "Week 1: Project Setup & Foundation",
      icon: Terminal,
      color: "bg-blue-500",
      goal: "Get all tools installed, repositories created, and basic 'Hello World' running on both frontend and backend",
      dailyPlan: [
        {
          day: "Day 1 - Monday",
          focus: "Development Environment Setup",
          timeEstimate: "2-3 hours",
          tasks: [
            {
              id: 1,
              title: "Install Required Software",
              steps: [
                "Install JDK 17 (from Oracle or OpenJDK)",
                "Install IntelliJ IDEA Community Edition",
                "Install Node.js (v18 or later) and npm",
                "Install VS Code with Angular extensions",
                "Install PostgreSQL (v14+) and pgAdmin 4",
                "Install Git and create GitHub account if needed",
                "Install Postman or Thunder Client for API testing"
              ],
              commands: [
                "# Verify installations",
                "java -version",
                "node -v && npm -v",
                "psql --version",
                "git --version"
              ]
            },
            {
              id: 2,
              title: "Create GitHub Repository",
              steps: [
                "Create new repo: 'life-os-app'",
                "Initialize with README.md",
                "Create .gitignore (Java, Node, Angular)",
                "Create folder structure: /backend, /frontend, /docs",
                "Write initial README with tech stack",
                "Push initial commit"
              ]
            }
          ],
          deliverable: "✅ All tools installed and verified, GitHub repo created"
        },
        {
          day: "Day 2 - Tuesday",
          focus: "Spring Boot Backend Setup",
          timeEstimate: "2-3 hours",
          tasks: [
            {
              id: 1,
              title: "Initialize Spring Boot Project",
              steps: [
                "Go to start.spring.io",
                "Project: Maven, Language: Java, Spring Boot: 3.2.x",
                "Group: com.lifeos, Artifact: backend",
                "Dependencies: Spring Web, Spring Data JPA, PostgreSQL Driver, Spring Security, Validation, Lombok",
                "Download and extract to /backend folder",
                "Open in IntelliJ IDEA"
              ]
            },
            {
              id: 2,
              title: "Configure Application Properties",
              steps: [
                "Rename application.properties to application.yml",
                "Configure server.port=8080",
                "Add spring.application.name=lifeos-backend",
                "Leave DB config empty for now (will add Day 3)"
              ],
              code: `# application.yml
server:
  port: 8080

spring:
  application:
    name: lifeos-backend`
            },
            {
              id: 3,
              title: "Create Package Structure",
              steps: [
                "Create packages: controller, service, repository, model, dto, config, exception, security",
                "Create TestController with @GetMapping('/test')",
                "Return 'Hello from Life OS Backend!'",
                "Run application and test http://localhost:8080/test"
              ]
            }
          ],
          deliverable: "✅ Spring Boot runs and responds to /test endpoint"
        },
        {
          day: "Day 3 - Wednesday",
          focus: "PostgreSQL Database Setup",
          timeEstimate: "2 hours",
          tasks: [
            {
              id: 1,
              title: "Create Database",
              steps: [
                "Open pgAdmin 4",
                "Create new database: 'lifeos_db'",
                "Create user: 'lifeos_user' with password",
                "Grant all privileges to lifeos_user on lifeos_db",
                "Note down connection details"
              ],
              commands: [
                "-- SQL Commands",
                "CREATE DATABASE lifeos_db;",
                "CREATE USER lifeos_user WITH PASSWORD 'your_password';",
                "GRANT ALL PRIVILEGES ON DATABASE lifeos_db TO lifeos_user;"
              ]
            },
            {
              id: 2,
              title: "Configure Spring Boot DB Connection",
              steps: [
                "Add datasource config to application.yml",
                "Set spring.jpa.hibernate.ddl-auto=create-drop (for dev)",
                "Enable SQL logging",
                "Create simple User entity to test connection",
                "Run app and verify tables are created in pgAdmin"
              ],
              code: `spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/lifeos_db
    username: lifeos_user
    password: your_password
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    properties:
      hibernate:
        format_sql: true`
            }
          ],
          deliverable: "✅ Database connected, test table created automatically"
        },
        {
          day: "Day 4 - Thursday",
          focus: "Angular Frontend Setup",
          timeEstimate: "2-3 hours",
          tasks: [
            {
              id: 1,
              title: "Create Angular Project",
              steps: [
                "Install Angular CLI: npm install -g @angular/cli",
                "Navigate to /frontend folder",
                "Run: ng new lifeos-frontend",
                "Choose: CSS, No SSR",
                "Install Angular Material: ng add @angular/material",
                "Choose theme: Indigo/Pink",
                "Run: ng serve and verify http://localhost:4200"
              ],
              commands: [
                "npm install -g @angular/cli",
                "ng new lifeos-frontend",
                "cd lifeos-frontend",
                "ng add @angular/material"
              ]
            },
            {
              id: 2,
              title: "Setup Project Structure",
              steps: [
                "Create folders: src/app/core, src/app/shared, src/app/features",
                "Generate modules: ng g module core",
                "Generate modules: ng g module shared",
                "Create environment files for API URL",
                "Clean up default app.component.html"
              ]
            },
            {
              id: 3,
              title: "Configure API Proxy",
              steps: [
                "Create proxy.conf.json in root",
                "Configure /api/* to forward to localhost:8080",
                "Update angular.json to use proxy",
                "Restart ng serve with --proxy-config flag"
              ],
              code: `// proxy.conf.json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true
  }
}`
            }
          ],
          deliverable: "✅ Angular app running with Material UI and API proxy configured"
        },
        {
          day: "Day 5 - Friday",
          focus: "Connect Frontend to Backend",
          timeEstimate: "2 hours",
          tasks: [
            {
              id: 1,
              title: "Create Test API Service",
              steps: [
                "Generate service: ng g service core/services/api",
                "Inject HttpClient",
                "Create getTest() method calling /api/test",
                "Create test component to display API response",
                "Verify full stack connection works"
              ],
              code: `// api.service.ts
getTest(): Observable<string> {
  return this.http.get('/api/test', { responseType: 'text' });
}`
            },
            {
              id: 2,
              title: "Setup CORS on Backend",
              steps: [
                "Create WebConfig class",
                "Add @Configuration annotation",
                "Configure CORS to allow localhost:4200",
                "Test API call from Angular",
                "Verify no CORS errors in browser console"
              ]
            },
            {
              id: 3,
              title: "Initial Git Commit",
              steps: [
                "Create .gitignore files (node_modules, target, etc)",
                "Stage all files: git add .",
                "Commit: git commit -m 'Initial project setup'",
                "Push to GitHub: git push origin main",
                "Verify on GitHub that both backend and frontend are there"
              ]
            }
          ],
          deliverable: "✅ Full stack running: Angular calls Spring Boot successfully"
        }
      ],
      weekendTasks: {
        title: "Weekend - Review & Prepare",
        tasks: [
          "Review all code written this week",
          "Read Spring Security documentation for JWT",
          "Watch tutorial on JWT authentication",
          "Plan Week 2 tasks in detail",
          "Set up a simple task tracker (Notion/Trello) for this project"
        ]
      },
      resources: [
        "Spring Boot Official Docs: spring.io/guides/gs/rest-service",
        "Angular Tour of Heroes: angular.io/tutorial",
        "PostgreSQL Tutorial: postgresqltutorial.com",
        "Git Basics: git-scm.com/book/en/v2/Getting-Started"
      ]
    },
    {
      id: 2,
      title: "Week 2: Authentication & User Management",
      icon: Shield,
      color: "bg-green-500",
      goal: "Implement complete JWT authentication flow - users can register, login, and access protected routes",
      dailyPlan: [
        {
          day: "Day 1 - Monday",
          focus: "User Entity & Repository",
          timeEstimate: "2-3 hours",
          tasks: [
            {
              id: 1,
              title: "Create User Entity",
              steps: [
                "Create User.java in model package",
                "Add fields: id (Long), username, email, password, createdAt",
                "Add @Entity, @Table(name = 'users')",
                "Use @Id @GeneratedValue(strategy = IDENTITY)",
                "Use Lombok @Data, @Builder, @NoArgsConstructor, @AllArgsConstructor",
                "Add @Column constraints (unique for email/username)",
                "Implement UserDetails interface for Spring Security"
              ],
              code: `@Entity
@Table(name = "users")
@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}`
            },
            {
              id: 2,
              title: "Create UserRepository",
              steps: [
                "Create UserRepository interface extending JpaRepository<User, Long>",
                "Add custom method: Optional<User> findByUsername(String username)",
                "Add custom method: Optional<User> findByEmail(String email)",
                "Add method: boolean existsByUsername(String username)",
                "Add method: boolean existsByEmail(String email)"
              ]
            }
          ],
          deliverable: "✅ User entity created, database table auto-generated"
        },
        {
          day: "Day 2 - Tuesday",
          focus: "JWT Utility & Configuration",
          timeEstimate: "3 hours",
          tasks: [
            {
              id: 1,
              title: "Add JWT Dependencies",
              steps: [
                "Add to pom.xml: io.jsonwebtoken:jjwt-api:0.11.5",
                "Add: io.jsonwebtoken:jjwt-impl:0.11.5",
                "Add: io.jsonwebtoken:jjwt-jackson:0.11.5",
                "Maven reload/update",
                "Verify dependencies downloaded"
              ]
            },
            {
              id: 2,
              title: "Create JwtUtil Class",
              steps: [
                "Create JwtUtil.java in security package",
                "Add @Component annotation",
                "Define secret key (use @Value from application.yml)",
                "Create generateToken(String username) method",
                "Create extractUsername(String token) method",
                "Create validateToken(String token, UserDetails) method",
                "Create isTokenExpired(String token) method",
                "Set expiration to 24 hours"
              ],
              code: `@Component
public class JwtUtil {
    @Value("\${jwt.secret}")
    private String secret;
    
    private final long JWT_EXPIRATION = 86400000; // 24 hours
    
    public String generateToken(String username) {
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
            .signWith(getSignKey(), SignatureAlgorithm.HS256)
            .compact();
    }
}`
            },
            {
              id: 3,
              title: "Configure Secret in application.yml",
              steps: [
                "Add jwt.secret property",
                "Generate secure random string (use online generator)",
                "Never commit real secrets (use environment variables for prod)"
              ]
            }
          ],
          deliverable: "✅ JWT utility class can generate and validate tokens"
        },
        {
          day: "Day 3 - Wednesday",
          focus: "Spring Security Configuration",
          timeEstimate: "3 hours",
          tasks: [
            {
              id: 1,
              title: "Create JwtAuthenticationFilter",
              steps: [
                "Create JwtAuthFilter extends OncePerRequestFilter",
                "Override doFilterInternal method",
                "Extract JWT from Authorization header",
                "Validate token and set authentication in SecurityContext",
                "Handle exceptions gracefully"
              ]
            },
            {
              id: 2,
              title: "Create SecurityConfig",
              steps: [
                "Create SecurityConfig class with @Configuration",
                "Add @EnableWebSecurity",
                "Create SecurityFilterChain bean",
                "Configure CORS",
                "Disable CSRF (for JWT)",
                "Set session to STATELESS",
                "Permit /auth/** endpoints",
                "Require authentication for all others",
                "Add JWT filter before UsernamePasswordAuthenticationFilter"
              ],
              code: `@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(csrf -> csrf.disable())
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**").permitAll()
            .anyRequest().authenticated()
        )
        .sessionManagement(session -> 
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
}`
            },
            {
              id: 3,
              title: "Create Password Encoder Bean",
              steps: [
                "Add BCryptPasswordEncoder bean in SecurityConfig",
                "Will be used to hash passwords before saving"
              ]
            }
          ],
          deliverable: "✅ Spring Security configured with JWT authentication"
        },
        {
          day: "Day 4 - Thursday",
          focus: "Auth Service & Controller",
          timeEstimate: "2-3 hours",
          tasks: [
            {
              id: 1,
              title: "Create DTOs",
              steps: [
                "Create RegisterRequest DTO (username, email, password)",
                "Create LoginRequest DTO (username, password)",
                "Create AuthResponse DTO (token, username, message)",
                "Add validation annotations (@NotBlank, @Email, @Size)"
              ]
            },
            {
              id: 2,
              title: "Create AuthService",
              steps: [
                "Create register() method",
                "Check if username/email exists",
                "Hash password with BCryptPasswordEncoder",
                "Save user to database",
                "Generate JWT token",
                "Return AuthResponse",
                "Create login() method",
                "Authenticate with AuthenticationManager",
                "Generate token on successful login"
              ]
            },
            {
              id: 3,
              title: "Create AuthController",
              steps: [
                "Create AuthController with @RestController",
                "Add @RequestMapping('/api/auth')",
                "Create POST /register endpoint",
                "Create POST /login endpoint",
                "Add @Valid for DTO validation",
                "Test with Postman"
              ]
            }
          ],
          deliverable: "✅ Register and login endpoints working"
        },
        {
          day: "Day 5 - Friday",
          focus: "Angular Auth Implementation",
          timeEstimate: "3 hours",
          tasks: [
            {
              id: 1,
              title: "Create Auth Module & Models",
              steps: [
                "Generate: ng g module features/auth --routing",
                "Create interfaces: User, LoginRequest, RegisterRequest, AuthResponse",
                "Generate auth service: ng g service features/auth/services/auth"
              ]
            },
            {
              id: 2,
              title: "Implement Auth Service",
              steps: [
                "Create register() method calling POST /api/auth/register",
                "Create login() method calling POST /api/auth/login",
                "Store JWT in localStorage",
                "Create logout() method to clear token",
                "Create isAuthenticated() method",
                "Create getToken() method"
              ]
            },
            {
              id: 3,
              title: "Create Login Component",
              steps: [
                "Generate: ng g component features/auth/login",
                "Create reactive form with username, password",
                "Add form validation",
                "Call authService.login() on submit",
                "Navigate to dashboard on success",
                "Display error messages",
                "Add Material UI form fields and buttons"
              ]
            },
            {
              id: 4,
              title: "Create Register Component",
              steps: [
                "Generate: ng g component features/auth/register",
                "Create form with username, email, password, confirmPassword",
                "Add password match validator",
                "Call authService.register()",
                "Navigate to login on success"
              ]
            }
          ],
          deliverable: "✅ Login and register forms working end-to-end"
        }
      ],
      weekendTasks: {
        title: "Weekend - Auth Guards & Interceptors",
        tasks: [
          "Create AuthGuard to protect routes",
          "Create HTTP Interceptor to attach JWT to requests",
          "Test full auth flow: register → login → access protected route",
          "Handle token expiration",
          "Add loading states and better error handling",
          "Test edge cases (wrong password, duplicate username, etc.)"
        ]
      },
      resources: [
        "JWT Introduction: jwt.io",
        "Spring Security Docs: spring.io/guides/topicals/spring-security-architecture",
        "Angular Forms Guide: angular.io/guide/reactive-forms",
        "BCrypt Explained: youtube.com/watch?v=O6cmuiTBZVs"
      ]
    },
    {
      id: 3,
      title: "Week 3: Task Management (Backend Focus)",
      icon: Database,
      color: "bg-purple-500",
      goal: "Build complete task CRUD operations with filtering, sorting, and pagination",
      dailyPlan: [
        {
          day: "Day 1-2",
          focus: "Task Entity & Repository",
          timeEstimate: "4-5 hours total",
          tasks: [
            {
              id: 1,
              title: "Create Task Entity",
              steps: [
                "Create Task.java with fields: id, title, description, priority (enum), status (enum), dueDate, userId",
                "Add @ManyToOne relationship with User",
                "Create enums: TaskPriority (LOW, MEDIUM, HIGH), TaskStatus (TODO, IN_PROGRESS, DONE)",
                "Add createdAt, updatedAt with @PrePersist, @PreUpdate"
              ]
            },
            {
              id: 2,
              title: "Create TaskRepository with Custom Queries",
              steps: [
                "Extend JpaRepository<Task, Long>",
                "Add: Page<Task> findByUserId(Long userId, Pageable pageable)",
                "Add: List<Task> findByUserIdAndStatus(Long userId, TaskStatus status)",
                "Add: @Query to find tasks due within next 7 days",
                "Add: @Query to find overdue tasks"
              ]
            }
          ]
        },
        {
          day: "Day 3",
          focus: "Task Service Layer",
          timeEstimate: "2-3 hours",
          tasks: [
            {
              id: 1,
              title: "Implement TaskService",
              steps: [
                "Create all CRUD methods",
                "Add authorization check (user can only access their tasks)",
                "Implement getTasksByUser with pagination",
                "Implement filtering by status, priority",
                "Add custom exception handling"
              ]
            }
          ]
        },
        {
          day: "Day 4-5",
          focus: "Task Controller & Testing",
          timeEstimate: "4-5 hours",
          tasks: [
            {
              id: 1,
              title: "Create TaskController",
              steps: [
                "GET /api/tasks - get all tasks for current user",
                "GET /api/tasks/{id} - get single task",
                "POST /api/tasks - create task",
                "PUT /api/tasks/{id} - update task",
                "DELETE /api/tasks/{id} - delete task",
                "Add pagination params (@PageableDefault)",
                "Add filter params (?status=TODO&priority=HIGH)"
              ]
            },
            {
              id: 2,
              title: "Test All Endpoints in Postman",
              steps: [
                "Create Postman collection",
                "Test CRUD with valid JWT token",
                "Test authorization (user B can't access user A's tasks)",
                "Test edge cases (invalid IDs, missing fields)"
              ]
            }
          ]
        }
      ],
      resources: [
        "Spring Data JPA: baeldung.com/spring-data-jpa-query",
        "Pagination: spring.io/guides/gs/accessing-data-jpa"
      ]
    },
    {
      id: 4,
      title: "Week 4: Task Management (Frontend) + Notes",
      icon: Layout,
      color: "bg-pink-500",
      goal: "Build task UI with Material table, forms, and start notes feature",
      dailyPlan: [
        {
          day: "Day 1-3",
          focus: "Task Management UI",
          timeEstimate: "6-7 hours total",
          tasks: [
            {
              id: 1,
              title: "Create Task Module",
              steps: [
                "Generate module, routing, service",
                "Create task-list component with Material table",
                "Create task-form component (dialog)",
                "Implement create, edit, delete operations",
                "Add status dropdown, priority badges",
                "Add pagination controls"
              ]
            }
          ]
        },
        {
          day: "Day 4-5",
          focus: "Notes Backend",
          timeEstimate: "4-5 hours",
          tasks: [
            {
              id: 1,
              title: "Create Note Entity & CRUD",
              steps: [
                "Create Note entity with title, content, tags",
                "Create Tag entity (ManyToMany)",
                "Build NoteRepository with search query",
                "Implement NoteService",
                "Create NoteController",
                "Test in Postman"
              ]
            }
          ]
        }
      ],
      resources: [
        "Angular Material Table: material.angular.io/components/table",
        "PostgreSQL Full-Text: postgresql.org/docs/current/textsearch"
      ]
    },
    {
      id: 5,
      title: "Week 5: Advanced Features",
      icon: Link,
      color: "bg-orange-500",
      goal: "Implement knowledge graph, dashboard, and goals tracking",
      dailyPlan: [
        {
          day: "Day 1-2",
          focus: "Note Linking System",
          timeEstimate: "4-5 hours",
          tasks: [
            {
              id: 1,
              title: "Build Knowledge Graph",
              steps: [
                "Create NoteLink entity",
                "Implement bi-directional linking",
                "Add graph traversal endpoint",
                "Frontend: integrate D3.js",
                "Create graph visualization component"
              ]
            }
          ]
        },
        {
          day: "Day 3-4",
          focus: "Dashboard & Analytics",
          timeEstimate: "4-5 hours",
          tasks: [
            {
              id: 1,
              title: "Build Dashboard",
              steps: [
                "Create stats endpoint",
                "Build widget grid layout",
                "Add charts (tasks by status, completion trends)",
                "Show upcoming tasks, recent notes"
              ]
            }
          ]
        },
        {
          day: "Day 5",
          focus: "Goals & Habits",
          timeEstimate: "3 hours",
          tasks: [
            {
              id: 1,
              title: "Basic Goal Tracking",
              steps: [
                "Create Goal entity",
                "Build simple CRUD",
                "Create goal tracker UI"
              ]
            }
          ]
        }
      ],
      resources: [
        "D3.js Tutorial: d3js.org",
        "Chart.js: chartjs.org/docs"
      ]
    },
    {
      id: 6,
      title: "Week 6: DevOps & AWS Deployment",
      icon: Cloud,
      color: "bg-red-500",
      goal: "Dockerize application and deploy to AWS with CI/CD",
      dailyPlan: [
        {
          day: "Day 1-2",
          focus: "Docker Setup",
          timeEstimate: "4-5 hours",
          tasks: [
            {
              id: 1,
              title: "Create Dockerfiles",
              steps: [
                "Multi-stage Dockerfile for Spring Boot",
                "Dockerfile for Angular with nginx",
                "Create docker-compose.yml",
                "Test local docker build",
                "Push images to Docker Hub"
              ]
            }
          ]
        },
        {
          day: "Day 3-4",
          focus: "AWS Infrastructure",
          timeEstimate: "5-6 hours",
          tasks: [
            {
              id: 1,
              title: "Setup AWS Resources",
              steps: [
                "Launch EC2 instance (t3.micro)",
                "Create RDS PostgreSQL",
                "Configure security groups",
                "Setup S3 bucket for frontend",
                "Configure domain (optional)"
              ]
            }
          ]
        },
        {
          day: "Day 5",
          focus: "CI/CD Pipeline",
          timeEstimate: "3-4 hours",
          tasks: [
            {
              id: 1,
              title: "GitHub Actions",
              steps: [
                "Create workflow files",
                "Setup automated tests",
                "Configure auto-deploy on push",
                "Test full pipeline"
              ]
            }
          ]
        }
      ],
      resources: [
        "Docker Tutorial: docker.com/101-tutorial",
        "AWS Free Tier: aws.amazon.com/free",
        "GitHub Actions: docs.github.com/actions"
      ]
    },
    {
      id: 7,
      title: "Week 7: Testing, Polish & Documentation",
      icon: TestTube,
      color: "bg-teal-500",
      goal: "Write tests, fix bugs, optimize performance, complete documentation",
      dailyPlan: [
        {
          day: "Day 1-2",
          focus: "Backend Testing",
          timeEstimate: "5 hours",
          tasks: [
            {
              id: 1,
              title: "Write Unit & Integration Tests",
              steps: [
                "JUnit tests for services",
                "Mockito for mocking",
                "Integration tests for controllers",
                "Aim for 60%+ code coverage"
              ]
            }
          ]
        },
        {
          day: "Day 3",
          focus: "Frontend Testing & Polish",
          timeEstimate: "3-4 hours",
          tasks: [
            {
              id: 1,
              title: "Add Polish",
              steps: [
                "Loading spinners",
                "Error messages",
                "Toast notifications",
                "Dark mode",
                "Responsive design"
              ]
            }
          ]
        },
        {
          day: "Day 4-5",
          focus: "Documentation",
          timeEstimate: "4-5 hours",
          tasks: [
            {
              id: 1,
              title: "Complete Documentation",
              steps: [
                "Write comprehensive README",
                "Add architecture diagram",
                "Document API with Swagger",
                "Create setup instructions",
                "Add screenshots/GIF demo",
                "Write blog post about the project"
              ]
            }
          ]
        }
      ],
      resources: [
        "JUnit 5: junit.org/junit5",
        "Swagger: springdoc.org",
        "README Best Practices: github.com/matiassingers/awesome-readme"
      ]
    }
  ];

  const getDayProgress = (day) => {
    const allTasks = day.tasks.flatMap(task => task.steps);
    const completed = allTasks.filter((_, idx) => 
      completedTasks[`${day.day}-${idx}`]
    ).length;
    return allTasks.length > 0 ? Math.round((completed / allTasks.length) * 100) : 0;
  };

  const currentWeek = weeks.find(w => w.id === selectedWeek);

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100">
      {/* Header */}
      <div className="bg-white shadow-md border-b sticky top-0 z-10">
        <div className="max-w-7xl mx-auto px-6 py-4">
          <h1 className="text-3xl font-bold text-gray-800">Life OS - Detailed Roadmap</h1>
          <p className="text-gray-600 mt-1">7-Week Day-by-Day Implementation Guide</p>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-6 py-8">
        {/* Week Selector */}
        <div className="grid grid-cols-7 gap-3 mb-8">
          {weeks.map((week) => {
            const Icon = week.icon;
            const isSelected = selectedWeek === week.id;
            
            return (
              <button
                key={week.id}
                onClick={() => setSelectedWeek(week.id)}
                className={`p-4 rounded-lg border-2 transition-all ${
                  isSelected
                    ? `${week.color} text-white border-transparent shadow-lg scale-105`
                    : 'bg-white text-gray-700 border-gray-200 hover:border-gray-300 hover:shadow'
                }`}
              >
                <Icon className="mx-auto mb-2" size={24} />
                <div className="text-xs font-semibold">Week {week.id}</div>
              </button>
            );
          })}
        </div>

        {/* Week Details */}
        <div className="bg-white rounded-xl shadow-lg p-8 mb-6">
          <div className="flex items-start gap-4 mb-6">
            <div className={`${currentWeek.color} p-4 rounded-lg`}>
              {React.createElement(currentWeek.icon, { className: "text-white", size: 32 })}
            </div>
            <div className="flex-1">
              <h2 className="text-2xl font-bold text-gray-800">{currentWeek.title}</h2>
              <p className="text-gray-600 mt-2">{currentWeek.goal}</p>
            </div>
          </div>

          {/* Daily Plan */}
          <div className="space-y-6">
            {currentWeek.dailyPlan.map((day, dayIdx) => {
              const progress = getDayProgress(day);
              
              return (
                <div key={dayIdx} className="border-l-4 border-gray-200 pl-6 pb-6">
                  <div className="mb-4">
                    <div className="flex items-center justify-between mb-2">
                      <h3 className="text-lg font-semibold text-gray-800 flex items-center gap-2">
                        <Calendar size={18} className="text-gray-500" />
                        {day.day}
                      </h3>
                      <span className="text-sm text-gray-500 flex items-center gap-1">
                        <Clock size={14} />
                        {day.timeEstimate}
                      </span>
                    </div>
                    <p className="text-sm font-medium text-gray-600 mb-3">Focus: {day.focus}</p>
                    
                    {/* Progress bar */}
                    <div className="flex items-center gap-3">
                      <div className="flex-1 bg-gray-200 rounded-full h-2">
                        <div
                          className={`${currentWeek.color} h-2 rounded-full transition-all duration-300`}
                          style={{ width: `${progress}%` }}
                        />
                      </div>
                      <span className="text-sm font-medium text-gray-600">{progress}%</span>
                    </div>
                  </div>

                  {/* Tasks */}
                  <div className="space-y-4">
                    {day.tasks.map((task, taskIdx) => (
                      <div key={taskIdx} className="bg-gray-50 rounded-lg p-4">
                        <h4 className="font-semibold text-gray-700 mb-3 flex items-center gap-2">
                          <FileText size={16} className="text-gray-500" />
                          {task.title}
                        </h4>
                        
                        <div className="space-y-2">
                          {task.steps.map((step, stepIdx) => {
                            const taskKey = `${day.day}-${taskIdx * 100 + stepIdx}`;
                            const isCompleted = completedTasks[taskKey];
                            
                            return (
                              <div
                                key={stepIdx}
                                onClick={() => toggleTask(day.day, taskIdx * 100 + stepIdx)}
                                className="flex items-start gap-3 cursor-pointer group"
                              >
                                {isCompleted ? (
                                  <CheckCircle className="text-green-500 mt-0.5 flex-shrink-0" size={18} />
                                ) : (
                                  <Circle className="text-gray-300 group-hover:text-gray-400 mt-0.5 flex-shrink-0" size={18} />
                                )}
                                <span className={`text-sm ${isCompleted ? 'text-gray-400 line-through' : 'text-gray-700'}`}>
                                  {step}
                                </span>
                              </div>
                            );
                          })}
                        </div>

                        {/* Code/Commands section */}
                        {task.commands && (
                          <div className="mt-3 bg-gray-800 text-gray-100 p-3 rounded text-xs font-mono overflow-x-auto">
                            {task.commands.map((cmd, idx) => (
                              <div key={idx}>{cmd}</div>
                            ))}
                          </div>
                        )}
                        
                        {task.code && (
                          <div className="mt-3 bg-gray-800 text-gray-100 p-3 rounded text-xs font-mono whitespace-pre overflow-x-auto">
                            {task.code}
                          </div>
                        )}
                      </div>
                    ))}
                  </div>

                  {/* Deliverable */}
                  {day.deliverable && (
                    <div className="mt-4 bg-green-50 border border-green-200 rounded-lg p-3">
                      <p className="text-sm font-medium text-green-800">{day.deliverable}</p>
                    </div>
                  )}
                </div>
              );
            })}
          </div>

          {/* Weekend Tasks */}
          {currentWeek.weekendTasks && (
            <div className="mt-6 bg-blue-50 border-2 border-blue-200 rounded-lg p-6">
              <h3 className="font-semibold text-blue-800 mb-3 flex items-center gap-2">
                <Calendar size={18} />
                {currentWeek.weekendTasks.title}
              </h3>
              <ul className="space-y-2">
                {currentWeek.weekendTasks.tasks.map((task, idx) => (
                  <li key={idx} className="text-sm text-blue-700 flex items-start gap-2">
                    <span className="text-blue-400">•</span>
                    {task}
                  </li>
                ))}
              </ul>
            </div>
          )}

          {/* Resources */}
          <div className="mt-6 bg-purple-50 border-2 border-purple-200 rounded-lg p-6">
            <h3 className="font-semibold text-purple-800 mb-3 flex items-center gap-2">
              <Book size={18} />
              Learning Resources
            </h3>
            <ul className="space-y-2">
              {currentWeek.resources.map((resource, idx) => (
                <li key={idx} className="text-sm text-purple-700">• {resource}</li>
              ))}
            </ul>
          </div>
        </div>

        {/* Navigation */}
        <div className="flex justify-between items-center">
          <button
            onClick={() => setSelectedWeek(Math.max(1, selectedWeek - 1))}
            disabled={selectedWeek === 1}
            className="px-6 py-3 bg-white border-2 border-gray-300 rounded-lg font-semibold text-gray-700 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            ← Previous Week
          </button>
          
          <div className="text-center">
            <div className="text-sm text-gray-500">Week {selectedWeek} of 7</div>
          </div>
          
          <button
            onClick={() => setSelectedWeek(Math.min(7, selectedWeek + 1))}
            disabled={selectedWeek === 7}
            className="px-6 py-3 bg-white border-2 border-gray-300 rounded-lg font-semibold text-gray-700 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Next Week →
          </button>
        </div>
      </div>
    </div>
  );
};

export default LifeOSRoadmap;