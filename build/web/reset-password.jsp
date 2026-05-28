<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reset Password - ProcureGov</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ministry-style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
    <link rel="stylesheet" href="procgov-dashboard.css">
    <style>
        .reset-password-container {
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }
        
        .reset-password-card {
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
        
        .password-requirements {
            font-size: 12px;
            color: #6c757d;
            margin-top: 8px;
        }
        
        .password-requirements ul {
            margin: 4px 0 0;
            padding-left: 20px;
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
        
        .btn-submit:disabled {
            opacity: 0.6;
            cursor: not-allowed;
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
        
        .match-indicator {
            font-size: 12px;
            margin-top: 4px;
        }
        
        .match-indicator.match {
            color: #28a745;
        }
        
        .match-indicator.mismatch {
            color: #dc3545;
        }
        
        .back-link {
            text-align: center;
            margin-top: 24px;
        }
        
        .back-link a {
            color: #6c757d;
            text-decoration: none;
            font-size: 14px;
        }
        
        .back-link a:hover {
            color: #00529B;
        }
    </style>
</head>
<body>

    <!-- Top Government Banner -->
    <div class="gov-banner"></div>

    <div class="reset-password-container">
        <div class="reset-password-card">
            
            <div class="card-header">
                <h2>Reset Password</h2>
                <p>Enter your new password below</p>
            </div>
            
            <div class="card-body">
                
                <!-- Error Messages -->
                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-error">
                        <span>!</span>
                        <span>${errorMessage}</span>
                    </div>
                </c:if>
                
                <c:if test="${not empty param.error}">
                    <div class="alert alert-error">
                        <span>!</span>
                        <span>${param.error}</span>
                    </div>
                </c:if>
                
                <!-- Reset Password Form -->
                <form action="${pageContext.request.contextPath}/reset-password" method="POST" id="resetPasswordForm">
                    
                    <input type="hidden" name="token" value="${param.token}">
                    
                    <div class="form-group">
                        <label for="newPassword">New Password</label>
                        <div class="input-wrapper">
                            <span class="input-icon">*</span>
                            <input type="password" 
                                   id="newPassword" 
                                   name="newPassword" 
                                   placeholder="Enter new password"
                                   minlength="8"
                                   required>
                        </div>
                        <div class="password-requirements">
                            Password must contain:
                            <ul>
                                <li id="req-length">At least 8 characters</li>
                                <li id="req-uppercase">One uppercase letter</li>
                                <li id="req-lowercase">One lowercase letter</li>
                                <li id="req-number">One number</li>
                            </ul>
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label for="confirmPassword">Confirm Password</label>
                        <div class="input-wrapper">
                            <span class="input-icon">*</span>
                            <input type="password" 
                                   id="confirmPassword" 
                                   name="confirmPassword" 
                                   placeholder="Confirm new password"
                                   required>
                        </div>
                        <div id="passwordMatch" class="match-indicator"></div>
                    </div>
                    
                    <button type="submit" class="btn-submit" id="submitBtn">Reset Password</button>
                    
                </form>
                
                <div class="back-link">
                    <a href="${pageContext.request.contextPath}/login.jsp">Back to Login</a>
                </div>
                
            </div>
        </div>
    </div>

    <script>
        // Password match validation
        const newPassword = document.getElementById('newPassword');
        const confirmPassword = document.getElementById('confirmPassword');
        const matchIndicator = document.getElementById('passwordMatch');
        const submitBtn = document.getElementById('submitBtn');
        
        function validatePassword(password) {
            const hasLength = password.length >= 8;
            const hasUpper = /[A-Z]/.test(password);
            const hasLower = /[a-z]/.test(password);
            const hasNumber = /[0-9]/.test(password);
            
            document.getElementById('req-length').style.color = hasLength ? '#28a745' : '#dc3545';
            document.getElementById('req-uppercase').style.color = hasUpper ? '#28a745' : '#dc3545';
            document.getElementById('req-lowercase').style.color = hasLower ? '#28a745' : '#dc3545';
            document.getElementById('req-number').style.color = hasNumber ? '#28a745' : '#dc3545';
            
            return hasLength && hasUpper && hasLower && hasNumber;
        }
        
        newPassword.addEventListener('input', function() {
            validatePassword(this.value);
            checkPasswordMatch();
        });
        
        confirmPassword.addEventListener('input', checkPasswordMatch);
        
        function checkPasswordMatch() {
            const pass = newPassword.value;
            const confirm = confirmPassword.value;
            
            if (confirm === '') {
                matchIndicator.textContent = '';
                matchIndicator.className = 'match-indicator';
            } else if (pass === confirm) {
                matchIndicator.textContent = 'Passwords match';
                matchIndicator.className = 'match-indicator match';
            } else {
                matchIndicator.textContent = 'Passwords do not match';
                matchIndicator.className = 'match-indicator mismatch';
            }
        }
        
        // Form validation
        document.getElementById('resetPasswordForm').addEventListener('submit', function(e) {
            const pass = newPassword.value;
            const confirm = confirmPassword.value;
            
            if (!validatePassword(pass)) {
                e.preventDefault();
                alert('Password does not meet requirements.');
                return false;
            }
            
            if (pass !== confirm) {
                e.preventDefault();
                alert('Passwords do not match.');
                return false;
            }
            
            submitBtn.disabled = true;
            submitBtn.textContent = 'Resetting...';
            
            return true;
        });
    </script>

</body>
</html>