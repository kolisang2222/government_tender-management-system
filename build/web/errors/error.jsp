<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/error.css">
    <link rel="stylesheet" href="procgov-dashboard.css">
    <title>Error - ProcureGov | Ministry of Public Works Lesotho</title>
</head>
<body>
    <main>
        <div>
            <div>
                <span>❌</span>
                <h3>An Error Has Occurred</h3>
            </div>
            
            <c:choose>
                <c:when test="${not empty errorMessage}">
                    <div>
                        <p>${errorMessage}</p>
                    </div>
                </c:when>
                <c:when test="${not empty requestScope['javax.servlet.error.message']}">
                    <div>
                        <p>${requestScope['javax.servlet.error.message']}</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div>
                        <p>An unexpected error has occurred while processing your request.</p>
                        <p>We apologize for the inconvenience.</p>
                    </div>
                </c:otherwise>
            </c:choose>
            
            <c:if test="${not empty requestScope['javax.servlet.error.status_code']}">
                <div>
                    <p><strong>Status Code:</strong> ${requestScope['javax.servlet.error.status_code']}</p>
                </div>
            </c:if>
            
            <div>
                <a href="javascript:history.back()">
                    <span>←</span>
                    <span>Go Back</span>
                </a>
                
                <a href="${pageContext.request.contextPath}/">
                    <span>🏠</span>
                    <span>Return to Home</span>
                </a>
                
                <c:choose>
                    <c:when test="${not empty sessionScope.user}">
                        <c:choose>
                            <c:when test="${sessionScope.userRole == 'SUPPLIER'}">
                                <a href="${pageContext.request.contextPath}/supplier/dashboard">
                                    <span>📊</span>
                                    <span>Supplier Dashboard</span>
                                </a>
                            </c:when>
                            <c:when test="${sessionScope.userRole == 'PROCUREMENT_OFFICER'}">
                                <a href="${pageContext.request.contextPath}/officer/dashboard">
                                    <span>📊</span>
                                    <span>Officer Dashboard</span>
                                </a>
                            </c:when>
                            <c:when test="${sessionScope.userRole == 'EVALUATION_COMMITTEE'}">
                                <a href="${pageContext.request.contextPath}/evaluator/dashboard">
                                    <span>📊</span>
                                    <span>Evaluator Dashboard</span>
                                </a>
                            </c:when>
                        </c:choose>
                        
                        <a href="${pageContext.request.contextPath}/logout">
                            <span>🚪</span>
                            <span>Logout</span>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/login.jsp">
                            <span>🔑</span>
                            <span>Login</span>
                        </a>
                    </c:otherwise>
                </c:choose>
            </div>
            
            <div>
                <p>Need assistance? Contact ICT Support:</p>
                <p>📞 +266 5740 8184 | 📧 kolisang.phatela@bothouniversity.com</p>
                <p>🕐 Monday - Friday, 08:00 - 16:30</p>
            </div>
            
            <c:if test="${not empty pageContext.exception}">
                <div>
                    <details>
                        <summary>Technical Information</summary>
                        <p><strong>Exception:</strong> ${pageContext.exception['class'].name}</p>
                        <p><strong>Message:</strong> ${pageContext.exception.message}</p>
                    </details>
                </div>
            </c:if>
        </div>
    </main>
</body>
</html>