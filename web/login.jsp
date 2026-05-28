<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="util.CSRFTokenUtil" %>
<%
    // Ensure CSRF token exists in session
    HttpSession sess = request.getSession(true);
    CSRFTokenUtil.getToken(sess);
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Login - ProcureGov | Ministry of Public Works Lesotho</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ministry-style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
    </head>
    <body class="auth-page">

        <main>

            <%-- LEFT SIDE: LOGIN CARD --%>
            <div class="login-card">

                <div class="card-header">
                    <h3>Secure Login Portal</h3>
                    <p>Access the ProcureGov tender management system</p>
                </div>

                <%-- ============================================= --%>
                <%-- MESSAGE DISPLAY SECTION                       --%>
                <%-- ============================================= --%>

                <%-- Password Reset Success --%>
                <c:if test="${param.reset == 'success'}">
                    <div class="alert alert-success">
                        <span>OK</span>
                        <span>Your password has been reset successfully. Please login.</span>
                        <button class="close-alert" onclick="this.parentElement.style.display = 'none'">&times;</button>
                    </div>
                </c:if>

                <c:if test="${not empty sessionScope.registrationSuccess}">
                    <div class="alert alert-success">
                        <span>OK</span>
                        <span><c:out value="${sessionScope.registrationSuccess}"/></span>
                        <button class="close-alert" onclick="this.parentElement.style.display = 'none'">&times;</button>
                    </div>
                    <c:remove var="registrationSuccess" scope="session"/>
                </c:if>

                <c:if test="${not empty sessionScope.logoutMessage}">
                    <div class="alert alert-success">
                        <span>OK</span>
                        <span><c:out value="${sessionScope.logoutMessage}"/></span>
                        <button class="close-alert" onclick="this.parentElement.style.display = 'none'">&times;</button>
                    </div>
                    <c:remove var="logoutMessage" scope="session"/>
                </c:if>

                <c:if test="${not empty requestScope.errorMessage}">
                    <div class="alert alert-error">
                        <span>!</span>
                        <span><c:out value="${requestScope.errorMessage}"/></span>
                        <button class="close-alert" onclick="this.parentElement.style.display = 'none'">&times;</button>
                    </div>
                </c:if>

                <c:if test="${not empty param.error}">
                    <div class="alert alert-error">
                        <span>!</span>
                        <span><c:out value="${param.error}"/></span>
                        <button class="close-alert" onclick="this.parentElement.style.display = 'none'">&times;</button>
                    </div>
                </c:if>

                <c:if test="${not empty param.timeout}">
                    <div class="alert alert-warning">
                        <span>!</span>
                        <span>Your session has expired. Please login again.</span>
                        <button class="close-alert" onclick="this.parentElement.style.display = 'none'">&times;</button>
                    </div>
                </c:if>

                <c:if test="${not empty param.denied}">
                    <div class="alert alert-error">
                        <span>!</span>
                        <span>Access Denied. Please login with appropriate credentials.</span>
                        <button class="close-alert" onclick="this.parentElement.style.display = 'none'">&times;</button>
                    </div>
                </c:if>

                <%-- Note: failedAttempts and accountLocked are now tracked in database, not session --%>

                <%-- ============================================= --%>
                <%-- LOGIN FORM                                   --%>
                <%-- ============================================= --%>

                <form action="${pageContext.request.contextPath}/login" method="POST">

                    <%-- CSRF Protection Token --%>
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                    <div class="form-group">
                        <label for="email">Email Address</label>
                        <div class="input-wrapper">
                            <span class="input-icon">@</span>
                            <input type="email" 
                                   id="email" 
                                   name="email" 
                                   placeholder="your.email@example.com"
                                   value="${param.email}"
                                   required
                                   autocomplete="email"
                                   autofocus>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="password">Password</label>
                        <div class="input-wrapper">
                            <span class="input-icon">*</span>
                            <input type="password" 
                                   id="password" 
                                   name="password" 
                                   placeholder="••••••••••"
                                   required
                                   autocomplete="current-password">
                        </div>
                    </div>

                    <div class="form-group">
                        <button type="submit" class="btn-primary">
                            <span>Sign In</span>
                            <span>→</span>
                        </button>
                    </div>

                </form>

                <div class="login-links">
                    <p>
                        <a href="${pageContext.request.contextPath}/forgot-password">
                            Forgot your password?
                        </a>
                    </p>
                    <p>
                        <a href="${pageContext.request.contextPath}/supplier/register">
                            Create a supplier account
                        </a>
                    </p>
                    <p class="staff-notice">
                        <small>Ministry staff accounts are created by administrators only.</small>
                    </p>
                </div>

            </div>

            <%-- RIGHT SIDE: INFO SIDEBAR --%>
            <div class="info-sidebar">

                <div class="info-card">
                    <h4>System Status</h4>
                    <p><span class="status-online">Operational</span></p>
                    <p>
                        <small>Server time: 
                            <jsp:useBean id="now" class="java.util.Date"/>
                            <fmt:formatDate value="${now}" pattern="dd MMM yyyy, HH:mm"/>
                        </small>
                    </p>
                </div>

                <div class="info-card">
                    <h4>User Roles</h4>
                    <div class="role-item">
                        <h5>Supplier</h5>
                        <p>Registered companies bidding on government tenders</p>
                    </div>
                    <div class="role-item">
                        <h5>Procurement Officer</h5>
                        <p>Ministry officials managing the tender lifecycle</p>
                    </div>
                    <div class="role-item">
                        <h5>Evaluation Committee</h5>
                        <p>Appointed officials scoring and evaluating bids</p>
                    </div>
                </div>

                <div class="info-card">
                    <h4>Need Help?</h4>
                    <p>+266 5740 8184</p>
                    <p>kolisang.phatela@bothouniversity.com</p>
                    <p>Mon-Fri, 08:00 - 16:30</p>
                </div>

            </div>

        </main>

        <script>
            // Clear error when user starts typing
            document.getElementById('email').addEventListener('input', function () {
                var alerts = document.querySelectorAll('.alert-error');
                alerts.forEach(function (alert) {
                    alert.style.display = 'none';
                });
            });

            document.getElementById('password').addEventListener('input', function () {
                var alerts = document.querySelectorAll('.alert-error');
                alerts.forEach(function (alert) {
                    alert.style.display = 'none';
                });
            });

            // Auto-fade alerts after 5 seconds
            setTimeout(function () {
                var alerts = document.querySelectorAll('.alert');
                alerts.forEach(function (alert) {
                    alert.style.transition = 'opacity 0.5s';
                    alert.style.opacity = '0';
                    setTimeout(function () {
                        alert.style.display = 'none';
                    }, 500);
                });
            }, 5000);
        </script>
    </body>
</html>