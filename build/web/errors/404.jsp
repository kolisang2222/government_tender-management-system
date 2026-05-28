<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/error.css">
    <link rel="stylesheet" href="procgov-dashboard.css">
    <title>404 Not Found - ProcureGov | Ministry of Public Works Lesotho</title>
</head>
<body>
    <main>
        <div>
            <div>
                <span>🔍</span>
                <span>404</span>
                <h3>Page Not Found</h3>
            </div>
            
            <div>
                <p>The page you are looking for could not be found.</p>
                <p>This may be because:</p>
                <ul>
                    <li>The page has been moved or deleted</li>
                    <li>You typed the URL incorrectly</li>
                    <li>The link you clicked is outdated</li>
                    <li>The resource does not exist</li>
                </ul>
            </div>
            
            <c:if test="${not empty requestScope['javax.servlet.error.request_uri']}">
                <div>
                    <p>Requested URL:</p>
                    <code>${requestScope['javax.servlet.error.request_uri']}</code>
                </div>
            </c:if>
            
            <div>
                <p>You can try the following:</p>
                
                <div>
                    <a href="javascript:history.back()">
                        <span>←</span>
                        <span>Go Back to Previous Page</span>
                    </a>
                    
                    <a href="${pageContext.request.contextPath}/">
                        <span>🏠</span>
                        <span>Go to Homepage</span>
                    </a>
                    
                    <c:choose>
                        <c:when test="${sessionScope.userRole == 'SUPPLIER'}">
                            <a href="${pageContext.request.contextPath}/supplier/dashboard">
                                <span>📊</span>
                                <span>Supplier Dashboard</span>
                            </a>
                            <a href="${pageContext.request.contextPath}/supplier/tenders">
                                <span>📋</span>
                                <span>Browse Tenders</span>
                            </a>
                        </c:when>
                        <c:when test="${sessionScope.userRole == 'PROCUREMENT_OFFICER'}">
                            <a href="${pageContext.request.contextPath}/officer/dashboard">
                                <span>📊</span>
                                <span>Officer Dashboard</span>
                            </a>
                            <a href="${pageContext.request.contextPath}/officer/tenders">
                                <span>📋</span>
                                <span>Manage Tenders</span>
                            </a>
                        </c:when>
                        <c:when test="${sessionScope.userRole == 'EVALUATION_COMMITTEE'}">
                            <a href="${pageContext.request.contextPath}/evaluator/dashboard">
                                <span>📊</span>
                                <span>Evaluator Dashboard</span>
                            </a>
                            <a href="${pageContext.request.contextPath}/evaluator/tenders">
                                <span>📋</span>
                                <span>Assigned Tenders</span>
                            </a>
                        </c:when>
                    </c:choose>
                </div>
            </div>
            
            <div>
                <p>If you believe this is an error, please contact ICT Support:</p>
                <p>📞 +266 5740 8184| 📧 kolisang.phatela@bothouniversity.com</p>
            </div>
        </div>
    </main>
</body>
</html>