<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/error.css">
    <link rel="stylesheet" href="procgov-dashboard.css">
    <title>403 Forbidden - ProcureGov | Ministry of Public Works Lesotho</title>
</head>
<body>
    <main>
        <div>
            <div>
                <span>🔒</span>
                <span>403</span>
                <h3>Access Denied</h3>
            </div>
            
            <div>
                <p>You do not have permission to access this resource.</p>
                <p>This may be due to:</p>
                <ul>
                    <li>Insufficient user privileges</li>
                    <li>Attempting to access restricted content</li>
                    <li>Session expired or invalid</li>
                    <li>Resource requires higher authorization level</li>
                </ul>
            </div>
            
            <c:choose>
                <c:when test="${not empty sessionScope.user}">
                    <div>
                        <p>You are currently logged in as:</p>
                        <p><strong>${sessionScope.userName}</strong> (${sessionScope.userRole})</p>
                        <p>This role does not have access to the requested resource.</p>
                    </div>
                    
                    <div>
                        <c:choose>
                            <c:when test="${sessionScope.userRole == 'SUPPLIER'}">
                                <a href="${pageContext.request.contextPath}/supplier/dashboard">
                                    <span>📊</span>
                                    <span>Go to Supplier Dashboard</span>
                                </a>
                            </c:when>
                            <c:when test="${sessionScope.userRole == 'PROCUREMENT_OFFICER'}">
                                <a href="${pageContext.request.contextPath}/officer/dashboard">
                                    <span>📊</span>
                                    <span>Go to Officer Dashboard</span>
                                </a>
                            </c:when>
                            <c:when test="${sessionScope.userRole == 'EVALUATION_COMMITTEE'}">
                                <a href="${pageContext.request.contextPath}/evaluator/dashboard">
                                    <span>📊</span>
                                    <span>Go to Evaluator Dashboard</span>
                                </a>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/">
                                    <span>🏠</span>
                                    <span>Return to Home</span>
                                </a>
                            </c:otherwise>
                        </c:choose>
                        
                        <a href="${pageContext.request.contextPath}/logout">
                            <span>🚪</span>
                            <span>Logout and Login as Different User</span>
                        </a>
                    </div>
                </c:when>
                
                <c:otherwise>
                    <div>
                        <p>You are not currently logged in.</p>
                        <p>Please login with appropriate credentials to access this resource.</p>
                    </div>
                    
                    <div>
                        <a href="${pageContext.request.contextPath}/login.jsp">
                            <span>🔑</span>
                            <span>Go to Login Page</span>
                        </a>
                        
                        <a href="${pageContext.request.contextPath}/supplier/register">
                            <span>📝</span>
                            <span>Register as Supplier</span>
                        </a>
                    </div>
                </c:otherwise>
            </c:choose>
            
            <div>
                <p>Need assistance? Contact ICT Support:</p>
                <p>📞 +266 5740 8184 | 📧 kolisang.phatela@bothouniversity.com</p>
                <p>🕐 Monday - Friday, 08:00 - 16:30</p>
            </div>
        </div>
    </main>
</body>
</html>