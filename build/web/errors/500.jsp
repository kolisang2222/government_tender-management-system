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
    <title>500 Server Error - ProcureGov | Ministry of Public Works Lesotho</title>
</head>
<body>
    <main>
        <div>
            <div>
                <span>⚠️</span>
                <span>500</span>
                <h3>Internal Server Error</h3>
            </div>
            
            <div>
                <p>An unexpected error has occurred on the server.</p>
                <p>We apologize for the inconvenience.</p>
                <p>Our technical team has been automatically notified of this issue.</p>
            </div>
            
            <div>
                <p>What you can do:</p>
                <ul>
                    <li>Refresh the page and try again</li>
                    <li>Clear your browser cache and cookies</li>
                    <li>Try again later</li>
                    <li>Contact ICT Support if the problem persists</li>
                </ul>
            </div>
            
            <div>
                <a href="javascript:location.reload()">
                    <span>🔄</span>
                    <span>Refresh Page</span>
                </a>
                
                <a href="${pageContext.request.contextPath}/">
                    <span>🏠</span>
                    <span>Return to Home</span>
                </a>
                
                <c:choose>
                    <c:when test="${sessionScope.userRole == 'SUPPLIER'}">
                        <a href="${pageContext.request.contextPath}/supplier/dashboard">
                            <span>📊</span>
                            <span>Go to Dashboard</span>
                        </a>
                    </c:when>
                    <c:when test="${sessionScope.userRole == 'PROCUREMENT_OFFICER'}">
                        <a href="${pageContext.request.contextPath}/officer/dashboard">
                            <span>📊</span>
                            <span>Go to Dashboard</span>
                        </a>
                    </c:when>
                    <c:when test="${sessionScope.userRole == 'EVALUATION_COMMITTEE'}">
                        <a href="${pageContext.request.contextPath}/evaluator/dashboard">
                            <span>📊</span>
                            <span>Go to Dashboard</span>
                        </a>
                    </c:when>
                </c:choose>
            </div>
            
            <c:if test="${not empty pageContext.exception}">
                <div>
                    <details>
                        <summary>Technical Details (for support staff)</summary>
                        <div>
                            <p><strong>Exception:</strong> ${pageContext.exception.class.name}</p>
                            <p><strong>Message:</strong> ${pageContext.exception.message}</p>
                            
                            <c:if test="${not empty requestScope['javax.servlet.error.request_uri']}">
                                <p><strong>Request URI:</strong> ${requestScope['javax.servlet.error.request_uri']}</p>
                            </c:if>
                            
                            <c:if test="${not empty requestScope['javax.servlet.error.servlet_name']}">
                                <p><strong>Servlet:</strong> ${requestScope['javax.servlet.error.servlet_name']}</p>
                            </c:if>
                            
                            <p><strong>Stack Trace:</strong></p>
                            <pre>
                                <c:forEach var="trace" items="${pageContext.exception.stackTrace}">
                                    ${trace}
                                </c:forEach>
                            </pre>
                        </div>
                    </details>
                </div>
            </c:if>
            
            <div>
                <p>ICT Support Contact Information:</p>
                <p>📞 +266 5740 8184</p>
                <p>📧 kolisang.phatela@bothouniversity.com</p>
                <p>🕐 Monday - Friday, 08:00 - 16:30</p>
            </div>
        </div>
    </main>

</body>
</html>