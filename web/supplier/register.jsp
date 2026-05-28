<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ministry-style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/register.css">
        <title>Supplier Registration - ProcureGov | Ministry of Public Works Lesotho</title>
    </head>
    <body>

        <header>
            <div>
                <div>
                    <div></div>
                    <div>
                        <h1>MINISTRY OF PUBLIC WORKS</h1>
                        <h2>KINGDOM OF LESOTHO</h2>
                        <p>ProcureGov — Government e-Tender Management System</p>
                    </div>
                </div>
                <div>
                    <p>Already have an account?</p>
                    <a href="${pageContext.request.contextPath}/login.jsp">Sign In</a>
                </div>
            </div>
        </header>

        <main>
            <div>
                <div>
                    <h3>Supplier Registration</h3>
                    <p>Register your company to bid on government tenders</p>
                </div>

                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-error">
                        <span>!</span>
                        <span>${errorMessage}</span>
                    </div>
                </c:if>

                <c:if test="${not empty successMessage}">
                    <div class="alert alert-success">
                        <span>OK</span>
                        <span>${successMessage}</span>
                    </div>
                </c:if>

                <form action="${pageContext.request.contextPath}/supplier/register" method="POST" id="registrationForm">

                    <%-- CSRF Protection Token --%>
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                    <div>
                        <label for="fullName">
                            Company / Individual Name
                            <span>*</span>
                        </label>
                        <div>
                            <span>🏢</span>
                            <input type="text" 
                                   id="fullName" 
                                   name="fullName" 
                                   value="${fullName}"
                                   placeholder="e.g., Lesotho Construction (Pty) Ltd"
                                   maxlength="100"
                                   required>
                        </div>
                        <small>Official registered business name or individual's full name</small>
                        <div id="fullNameError"></div>
                    </div>

                    <div>
                        <label for="email">
                            Email Address
                            <span>*</span>
                        </label>
                        <div>
                            <span>📧</span>
                            <input type="email" 
                                   id="email" 
                                   name="email" 
                                   value="${email}"
                                   placeholder="info@yourcompany.co.ls"
                                   maxlength="100"
                                   required>
                        </div>
                        <small>This will be your login username</small>
                        <div id="emailError"></div>
                    </div>

                    <div>
                        <label for="address">
                            Physical Address
                            <span>*</span>
                        </label>
                        <div>
                            <span>📍</span>
                            <textarea id="address" 
                                      name="address" 
                                      rows="3"
                                      placeholder="Plot 1234, Industrial Area&#10;Maseru 100&#10;Lesotho"
                                      required>${address}</textarea>
                        </div>
                        <small>Your registered business location in Lesotho</small>
                        <div id="addressError"></div>
                    </div>

                    <div>
                        <label for="contactNumber">
                            Contact Number
                            <span>*</span>
                        </label>
                        <div>
                            <span>📞</span>
                            <input type="tel" 
                                   id="contactNumber" 
                                   name="contactNumber" 
                                   value="${contactNumber}"
                                   placeholder="+266 2231 5678"
                                   required>
                        </div>
                        <small>Include country code (+266)</small>
                        <div id="contactNumberError"></div>
                    </div>

                    <div>
                        <label for="password">
                            Password
                            <span>*</span>
                        </label>
                        <div>
                            <span>🔒</span>
                            <input type="password" 
                                   id="password" 
                                   name="password"
                                   placeholder="••••••••••"
                                   minlength="8"
                                   required>
                            <button type="button" id="togglePassword">👁️</button>
                        </div>

                        <div id="passwordStrength">
                            <div>
                                <div id="strengthBar"></div>
                            </div>
                            <span id="strengthText">Enter a password</span>
                        </div>

                        <div>
                            <p>Password must contain:</p>
                            <ul>
                                <li id="req-length">At least 8 characters</li>
                                <li id="req-uppercase">One uppercase letter</li>
                                <li id="req-lowercase">One lowercase letter</li>
                                <li id="req-number">One number</li>
                                <li id="req-special">One special character</li>
                            </ul>
                        </div>
                        <div id="passwordError"></div>
                    </div>

                    <div>
                        <label for="confirmPassword">
                            Confirm Password
                            <span>*</span>
                        </label>
                        <div>
                            <span>🔒</span>
                            <input type="password" 
                                   id="confirmPassword" 
                                   name="confirmPassword"
                                   placeholder="••••••••••"
                                   required>
                        </div>
                        <div id="confirmPasswordError"></div>
                        <div id="passwordMatch"></div>
                    </div>

                    <div>
                        <label>
                            <input type="checkbox" name="agreeTerms" id="agreeTerms" required>
                            <span>
                                I agree to the 
                                <a href="#" onclick="return false;">Terms and Conditions</a>
                                and 
                                <a href="#" onclick="return false;">Privacy Policy</a>
                                <span>*</span>
                            </span>
                        </label>
                    </div>

                    <div>
                        <label>
                            <input type="checkbox" name="confirmAccuracy" id="confirmAccuracy" required>
                            <span>
                                I confirm that all information provided is accurate and complete
                                <span>*</span>
                            </span>
                        </label>
                    </div>

                    <div>
                        <button type="submit" id="submitBtn">
                            <span>Register as Supplier</span>
                            <span>→</span>
                        </button>

                        <a href="${pageContext.request.contextPath}/login.jsp">
                            Cancel
                        </a>
                    </div>

                    <div>
                        <p>
                            Already registered?
                            <a href="${pageContext.request.contextPath}/login.jsp">
                                Login here
                            </a>
                        </p>
                    </div>

                </form>
            </div>

            <aside>
                <div>
                    <h4>Why Register?</h4>
                    <ul>
                        <li>Browse and download tender notices</li>
                        <li>Submit sealed electronic bids</li>
                        <li>Track your bid status in real-time</li>
                        <li>Receive award notifications</li>
                        <li>Access government contracts</li>
                    </ul>
                </div>

                <div>
                    <h4>Requirements</h4>
                    <ul>
                        <li>Valid Lesotho business registration</li>
                        <li>Tax clearance certificate</li>
                        <li>Valid email address</li>
                        <li>Physical address in Lesotho</li>
                    </ul>
                </div>

                <div>
                    <h4>Need Help?</h4>
                    <p>📞 +266 5740 8184</p>
                    <p>📧 kolisang.phatela@bothouniversity.com</p>
                    <p>🕐 Mon-Fri, 08:00 - 16:30</p>
                </div>

                <div>
                    <h4>Registration Process</h4>
                    <ol>
                        <li>Complete registration form</li>
                        <li>Verify email address</li>
                        <li>Wait for admin approval</li>
                        <li>Start bidding on tenders</li>
                    </ol>
                </div>
            </aside>
        </main>

        <jsp:include page="/shared/footer.jsp"/>

        <script>
            document.addEventListener('DOMContentLoaded', function () {

                var passwordInput = document.getElementById('password');
                var confirmInput = document.getElementById('confirmPassword');
                var toggleBtn = document.getElementById('togglePassword');
                var form = document.getElementById('registrationForm');

                if (toggleBtn) {
                    toggleBtn.addEventListener('click', function () {
                        var type = passwordInput.type === 'password' ? 'text' : 'password';
                        passwordInput.type = type;
                        this.textContent = type === 'password' ? '👁️' : '🙈';
                    });
                }

                if (passwordInput) {
                    passwordInput.addEventListener('input', function () {
                        validatePasswordStrength(this.value);
                    });
                }

                if (confirmInput) {
                    confirmInput.addEventListener('input', function () {
                        validatePasswordMatch();
                    });
                }

                if (form) {
                    form.addEventListener('submit', function (e) {
                        if (!validateRegistrationForm()) {
                            e.preventDefault();
                        } else {
                            // Disable submit button to prevent double submission
                            document.getElementById('submitBtn').disabled = true;
                            document.getElementById('submitBtn').innerHTML = '<span>Processing...</span>';
                        }
                    });
                }
            });

            function validatePasswordStrength(password) {
                var strengthBar = document.getElementById('strengthBar');
                var strengthText = document.getElementById('strengthText');

                var hasLength = password.length >= 8;
                var hasUpper = /[A-Z]/.test(password);
                var hasLower = /[a-z]/.test(password);
                var hasNumber = /[0-9]/.test(password);
                var hasSpecial = /[!@#$%^&*(),.?":{}|<>]/.test(password);

                var strength = 0;
                if (hasLength)
                    strength++;
                if (hasUpper)
                    strength++;
                if (hasLower)
                    strength++;
                if (hasNumber)
                    strength++;
                if (hasSpecial)
                    strength++;

                var percentage = (strength / 5) * 100;
                if (strengthBar) {
                    strengthBar.style.width = percentage + '%';

                    if (strength <= 2) {
                        strengthBar.style.background = '#dc3545';
                        strengthText.textContent = 'Weak password';
                    } else if (strength <= 3) {
                        strengthBar.style.background = '#ffc107';
                        strengthText.textContent = 'Fair password';
                    } else if (strength <= 4) {
                        strengthBar.style.background = '#17a2b8';
                        strengthText.textContent = 'Good password';
                    } else {
                        strengthBar.style.background = '#28a745';
                        strengthText.textContent = 'Strong password';
                    }
                }

                document.getElementById('req-length').style.color = hasLength ? '#28a745' : '#dc3545';
                document.getElementById('req-uppercase').style.color = hasUpper ? '#28a745' : '#dc3545';
                document.getElementById('req-lowercase').style.color = hasLower ? '#28a745' : '#dc3545';
                document.getElementById('req-number').style.color = hasNumber ? '#28a745' : '#dc3545';
                document.getElementById('req-special').style.color = hasSpecial ? '#28a745' : '#dc3545';
            }

            function validatePasswordMatch() {
                var password = document.getElementById('password').value;
                var confirm = document.getElementById('confirmPassword').value;
                var matchDiv = document.getElementById('passwordMatch');

                if (confirm.length === 0) {
                    matchDiv.textContent = '';
                    return true;
                }

                if (password === confirm) {
                    matchDiv.textContent = '✓ Passwords match';
                    matchDiv.style.color = '#28a745';
                    return true;
                } else {
                    matchDiv.textContent = '✗ Passwords do not match';
                    matchDiv.style.color = '#dc3545';
                    return false;
                }
            }

            function showError(fieldId, message) {
                var errorDiv = document.getElementById(fieldId + 'Error');
                if (errorDiv) {
                    errorDiv.textContent = message;
                    errorDiv.style.display = 'block';
                    errorDiv.style.color = '#dc3545';
                    errorDiv.style.fontSize = '0.85rem';
                    errorDiv.style.marginTop = '4px';
                }
                document.getElementById(fieldId).style.borderColor = '#dc3545';
            }

            function clearError(fieldId) {
                var errorDiv = document.getElementById(fieldId + 'Error');
                if (errorDiv) {
                    errorDiv.textContent = '';
                    errorDiv.style.display = 'none';
                }
                document.getElementById(fieldId).style.borderColor = '#dee2e6';
            }

            function validateRegistrationForm() {
                var isValid = true;

                var fullName = document.getElementById('fullName').value.trim();
                if (fullName.length < 3) {
                    showError('fullName', 'Name must be at least 3 characters');
                    isValid = false;
                } else {
                    clearError('fullName');
                }

                var email = document.getElementById('email').value.trim();
                var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                if (!emailRegex.test(email)) {
                    showError('email', 'Please enter a valid email address');
                    isValid = false;
                } else {
                    clearError('email');
                }

                var address = document.getElementById('address').value.trim();
                if (address.length < 10) {
                    showError('address', 'Please enter your full physical address');
                    isValid = false;
                } else {
                    clearError('address');
                }

                var contactNumber = document.getElementById('contactNumber').value.trim();
                var phoneRegex = /^\+?[0-9\s\-\(\)]{8,20}$/;
                if (!phoneRegex.test(contactNumber)) {
                    showError('contactNumber', 'Please enter a valid contact number');
                    isValid = false;
                } else {
                    clearError('contactNumber');
                }

                var password = document.getElementById('password').value;
                var hasLength = password.length >= 8;
                var hasUpper = /[A-Z]/.test(password);
                var hasLower = /[a-z]/.test(password);
                var hasNumber = /[0-9]/.test(password);
                var hasSpecial = /[!@#$%^&*(),.?":{}|<>]/.test(password);

                if (!(hasLength && hasUpper && hasLower && hasNumber && hasSpecial)) {
                    showError('password', 'Password does not meet requirements');
                    isValid = false;
                } else {
                    clearError('password');
                }

                if (!validatePasswordMatch()) {
                    showError('confirmPassword', 'Passwords do not match');
                    isValid = false;
                } else {
                    clearError('confirmPassword');
                }

                if (!document.getElementById('agreeTerms').checked) {
                    alert('You must agree to the Terms and Conditions');
                    isValid = false;
                }

                if (!document.getElementById('confirmAccuracy').checked) {
                    alert('You must confirm the accuracy of your information');
                    isValid = false;
                }

                return isValid;
            }
        </script>

    </body>
</html>