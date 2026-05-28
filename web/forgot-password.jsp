<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Forgot Password - ProcureGov</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ministry-style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
    <link rel="stylesheet" href="procgov-dashboard.css">
    <style>
        .forgot-password-container {
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }
        
        .forgot-password-card {
            background: white;
            border-radius: 16px;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
            width: 100%;
            max-width: 480px;
            overflow: hidden;
            border-top: 4px solid #00529B;
        }
        
        .card-header {
            padding: 32px 32px 16px;
            text-align: center;
        }
        
        .card-header h2 {
            margin: 0 0 8px;
            color: #1a1a2e;
            font-size: 24px;
        }
        
        .card-header p {
            margin: 0;
            color: #6c757d;
            font-size: 14px;
        }
        
        .card-body {
            padding: 16px 32px 32px;
        }
        
        .form-group {
            margin-bottom: 24px;
        }
        
        .form-group label {
            display: block;
            font-weight: 600;
            color: #1a1a2e;
            margin-bottom: 8px;
            font-size: 14px;
        }
        
        .input-wrapper {
            position: relative;
        }
        
        .input-icon {
            position: absolute;
            left: 14px;
            top: 50%;
            transform: translateY(-50%);
            color: #6c757d;
            font-size: 16px;
        }
        
        .input-wrapper input {
            width: 100%;
            padding: 14px 14px 14px 46px;
            border: 2px solid #dee2e6;
            border-radius: 8px;
            font-size: 15px;
            transition: border-color 0.2s;
        }
        
        .input-wrapper input:focus {
            outline: none;
            border-color: #00529B;
        }
        
        .btn-submit {
            width: 100%;
            padding: 14px;
            background: #00529B;
            color: white;
            border: none;
            border-radius: 8px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: background 0.2s;
        }
        
        .btn-submit:hover {
            background: #003d73;
        }
        
        .back-to-login {
            text-align: center;
            margin-top: 24px;
            padding-top: 24px;
            border-top: 1px solid #e9ecef;
        }
        
        .back-to-login a {
            color: #00529B;
            text-decoration: none;
            font-weight: 500;
        }
        
        .back-to-login a:hover {
            text-decoration: underline;
        }
        
        .alert {
            padding: 14px 16px;
            border-radius: 8px;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            gap: 12px;
        }
        
        .alert-success {
            background: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        
        .alert-error {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        
        .alert-info {
            background: #cce5ff;
            color: #004085;
            border: 1px solid #b8daff;
        }
        
        .info-box {
            background: #e6f0fa;
            border-radius: 8px;
            padding: 16px;
            margin-bottom: 24px;
            font-size: 13px;
            color: #495057;
        }
        
        .info-box strong {
            display: block;
            margin-bottom: 8px;
            color: #1a1a2e;
        }
    </style>
</head>
<body>

    <!-- Top Government Banner -->
    <div class="gov-banner"></div>

    <div class="forgot-password-container">
        <div class="forgot-password-card">
            
            <div class="card-header">
                <h2>Forgot Password</h2>
                <p>Enter your email to reset your password</p>
            </div>
            
            <div class="card-body">
                
                <!-- Success/Error Messages -->
                <c:if test="${not empty successMessage}">
                    <div class="alert alert-success">
                        <span>OK</span>
                        <span>${successMessage}</span>
                    </div>
                </c:if>
                
                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-error">
                        <span>!</span>
                        <span>${errorMessage}</span>
                    </div>
                </c:if>
                
                <c:if test="${not empty param.sent}">
                    <div class="alert alert-success">
                        <span>OK</span>
                        <span>Password reset instructions have been sent to your email.</span>
                    </div>
                </c:if>
                
                <c:if test="${not empty param.error}">
                    <div class="alert alert-error">
                        <span>!</span>
                        <span>${param.error}</span>
                    </div>
                </c:if>
                
                <c:if test="${not empty param.expired}">
                    <div class="alert alert-error">
                        <span>!</span>
                        <span>The reset link has expired. Please request a new one.</span>
                    </div>
                </c:if>
                
                <c:if test="${not empty param.invalid}">
                    <div class="alert alert-error">
                        <span>!</span>
                        <span>Invalid reset token. Please request a new password reset.</span>
                    </div>
                </c:if>
                
                <!-- Info Box -->
                <div class="info-box">
                    <strong>Password Reset Instructions</strong>
                    Enter the email address associated with your ProcureGov account. 
                    We will send you a link to reset your password. The link will expire in 30 minutes.
                </div>
                
                <!-- Forgot Password Form -->
                <form action="${pageContext.request.contextPath}/forgot-password" method="POST" id="forgotPasswordForm">
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
                                   autofocus>
                        </div>
                    </div>
                    
                    <button type="submit" class="btn-submit">Send Reset Link</button>
                    
                </form>
                
                <div class="back-to-login">
                    <a href="${pageContext.request.contextPath}/login.jsp">Back to Login</a>
                </div>
                
            </div>
        </div>
    </div>

    <script>
        // Form validation
        document.getElementById('forgotPasswordForm').addEventListener('submit', function(e) {
            const email = document.getElementById('email').value.trim();
            
            if (email === '') {
                e.preventDefault();
                alert('Please enter your email address.');
                return false;
            }
            
            if (!email.includes('@') || !email.includes('.')) {
                e.preventDefault();
                alert('Please enter a valid email address.');
                return false;
            }
            
            // Disable button to prevent double submission
            const btn = this.querySelector('.btn-submit');
            btn.disabled = true;
            btn.textContent = 'Sending...';
            
            return true;
        });
        
        // Auto-fade alerts after 5 seconds
        setTimeout(function() {
            const alerts = document.querySelectorAll('.alert');
            alerts.forEach(function(alert) {
                alert.style.transition = 'opacity 0.5s';
                alert.style.opacity = '0';
                setTimeout(function() {
                    alert.style.display = 'none';
                }, 500);
            });
        }, 5000);
    </script>

</body>
</html>