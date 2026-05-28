<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>My Profile - ProcureGov</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ministry-style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/supplier-profile.css">
        <link rel="stylesheet" href="procgov-dashboard.css">
    </head>
    <body>

        <jsp:include page="/shared/navigation.jsp" />

        <div class="dashboard-layout">

            <jsp:include page="/shared/sidebar.jsp" />

            <main class="dashboard-main">

                <!-- Page Header -->
                <div class="page-header">
                    <div class="header-left">
                        <h1>My Profile</h1>
                        <p>View and manage your company information</p>
                    </div>
                </div>

                <!-- Success/Error Messages -->
                <c:if test="${not empty sessionScope.successMessage}">
                    <div class="alert alert-success">
                        <span>OK</span>
                        <span>${sessionScope.successMessage}</span>
                        <button class="close-alert" onclick="this.parentElement.remove()">&times;</button>
                    </div>
                    <c:remove var="successMessage" scope="session" />
                </c:if>

                <c:if test="${not empty sessionScope.errorMessage}">
                    <div class="alert alert-error">
                        <span>!</span>
                        <span>${sessionScope.errorMessage}</span>
                        <button class="close-alert" onclick="this.parentElement.remove()">&times;</button>
                    </div>
                    <c:remove var="errorMessage" scope="session" />
                </c:if>

                <c:if test="${not empty validationErrors}">
                    <div class="alert alert-error">
                        <span>!</span>
                        <div>
                            <strong>Please correct the following errors:</strong>
                            <ul style="margin-top: 8px; padding-left: 20px;">
                                <c:forEach var="error" items="${validationErrors}">
                                    <li>${error}</li>
                                    </c:forEach>
                            </ul>
                        </div>
                    </div>
                </c:if>

                <!-- Profile Tabs -->
                <div class="profile-tabs">
                    <a href="#company" class="tab-link active" data-tab="company">Company Information</a>
                    <a href="#contact" class="tab-link" data-tab="contact">Contact Person</a>
                    <a href="#documents" class="tab-link" data-tab="documents">Documents</a>
                    <a href="#security" class="tab-link" data-tab="security">Security</a>
                </div>

                <!-- Company Information Tab -->
                <div id="company" class="tab-content active">
                    <div class="profile-card">
                        <div class="card-header">
                            <h2>Company Information</h2>
                            <button type="button" class="btn btn-outline" onclick="enableEdit('companyForm')">Edit</button>
                        </div>

                        <form id="companyForm" action="${pageContext.request.contextPath}/supplier/profile" method="POST">
                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                            <input type="hidden" name="action" value="updateCompany">
                            <input type="hidden" name="supplierId" value="${supplier.userId}">

                            <div class="form-grid">
                                <div class="form-group">
                                    <label for="registrationNumber">Registration Number</label>
                                    <input type="text" id="registrationNumber" value="${supplier.registrationNumber}" readonly disabled>
                                    <small>Auto-generated - Cannot be changed</small>
                                </div>

                                <div class="form-group">
                                    <label for="companyName">Company / Individual Name <span class="required">*</span></label>
                                    <input type="text" id="companyName" name="fullName" value="${supplier.fullName}" required disabled>
                                </div>

                                <div class="form-group">
                                    <label for="email">Email Address <span class="required">*</span></label>
                                    <input type="email" id="email" name="email" value="${supplier.email}" required disabled>
                                    <small>This is also your login username</small>
                                </div>

                                <div class="form-group">
                                    <label for="contactNumber">Contact Number <span class="required">*</span></label>
                                    <input type="tel" id="contactNumber" name="contactNumber" value="${supplier.contactNumber}" required disabled>
                                </div>

                                <div class="form-group full-width">
                                    <label for="address">Physical Address <span class="required">*</span></label>
                                    <textarea id="address" name="address" rows="3" required disabled>${supplier.address}</textarea>
                                </div>

                                <div class="form-group">
                                    <label for="taxNumber">Tax Clearance Number</label>
                                    <input type="text" id="taxNumber" name="taxNumber" value="${supplier.taxNumber}" disabled>
                                </div>

                                <div class="form-group">
                                    <label for="businessLicense">Business License Number</label>
                                    <input type="text" id="businessLicense" name="businessLicense" value="${supplier.businessLicense}" disabled>
                                </div>

                                <div class="form-group">
                                    <label for="yearsInBusiness">Years in Business</label>
                                    <input type="number" id="yearsInBusiness" name="yearsInBusiness" value="${supplier.yearsInBusiness}" min="0" disabled>
                                </div>

                                <div class="form-group">
                                    <label for="employeeCount">Number of Employees</label>
                                    <input type="number" id="employeeCount" name="employeeCount" value="${supplier.employeeCount}" min="1" disabled>
                                </div>
                            </div>

                            <div class="form-actions" style="display: none;">
                                <button type="submit" class="btn btn-primary">Save Changes</button>
                                <button type="button" class="btn btn-secondary" onclick="cancelEdit('companyForm')">Cancel</button>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- Contact Person Tab -->
                <div id="contact" class="tab-content">
                    <div class="profile-card">
                        <div class="card-header">
                            <h2>Contact Person</h2>
                            <button type="button" class="btn btn-outline" onclick="enableEdit('contactForm')">Edit</button>
                        </div>

                        <form id="contactForm" action="${pageContext.request.contextPath}/supplier/profile" method="POST">
                            <input type="hidden" name="action" value="updateContact">
                            <input type="hidden" name="supplierId" value="${supplier.userId}">

                            <div class="form-grid">
                                <div class="form-group">
                                    <label for="contactName">Full Name <span class="required">*</span></label>
                                    <input type="text" id="contactName" name="contactName" value="${supplier.contactName}" required disabled>
                                </div>

                                <div class="form-group">
                                    <label for="contactPosition">Position / Title</label>
                                    <input type="text" id="contactPosition" name="contactPosition" value="${supplier.contactPosition}" disabled>
                                </div>

                                <div class="form-group">
                                    <label for="contactEmail">Email Address <span class="required">*</span></label>
                                    <input type="email" id="contactEmail" name="contactEmail" value="${supplier.contactEmail}" required disabled>
                                </div>

                                <div class="form-group">
                                    <label for="contactPhone">Phone Number <span class="required">*</span></label>
                                    <input type="tel" id="contactPhone" name="contactPhone" value="${supplier.contactPhone}" required disabled>
                                </div>

                                <div class="form-group">
                                    <label for="alternativePhone">Alternative Phone</label>
                                    <input type="tel" id="alternativePhone" name="alternativePhone" value="${supplier.alternativePhone}" disabled>
                                </div>
                            </div>

                            <div class="form-actions" style="display: none;">
                                <button type="submit" class="btn btn-primary">Save Changes</button>
                                <button type="button" class="btn btn-secondary" onclick="cancelEdit('contactForm')">Cancel</button>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- Documents Tab -->
                <div id="documents" class="tab-content">
                    <div class="profile-card">
                        <div class="card-header">
                            <h2>Company Documents</h2>
                        </div>

                        <div class="documents-list">
                            <div class="document-item">
                                <div class="document-info">
                                    <span class="document-icon">PDF</span>
                                    <div class="document-details">
                                        <span class="document-name">Tax Clearance Certificate</span>
                                        <span class="document-meta">
                                            <c:choose>
                                                <c:when test="${not empty supplier.taxCertificatePath}">
                                                    Uploaded: ${supplier.taxCertificateDate}
                                                </c:when>
                                                <c:otherwise>
                                                    Not uploaded
                                                </c:otherwise>
                                            </c:choose>
                                        </span>
                                    </div>
                                </div>
                                <div class="document-actions">
                                    <c:choose>
                                        <c:when test="${not empty supplier.taxCertificatePath}">
                                            <a href="${pageContext.request.contextPath}/supplier/download-document?type=tax" class="btn-icon">View</a>
                                        </c:when>
                                        <c:otherwise>
                                            <button type="button" class="btn btn-outline" onclick="showUploadModal('tax')">Upload</button>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>

                            <div class="document-item">
                                <div class="document-info">
                                    <span class="document-icon">PDF</span>
                                    <div class="document-details">
                                        <span class="document-name">Business License</span>
                                        <span class="document-meta">
                                            <c:choose>
                                                <c:when test="${not empty supplier.licensePath}">
                                                    Uploaded: ${supplier.licenseDate}
                                                </c:when>
                                                <c:otherwise>
                                                    Not uploaded
                                                </c:otherwise>
                                            </c:choose>
                                        </span>
                                    </div>
                                </div>
                                <div class="document-actions">
                                    <c:choose>
                                        <c:when test="${not empty supplier.licensePath}">
                                            <a href="${pageContext.request.contextPath}/supplier/download-document?type=license" class="btn-icon">View</a>
                                        </c:when>
                                        <c:otherwise>
                                            <button type="button" class="btn btn-outline" onclick="showUploadModal('license')">Upload</button>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>

                            <div class="document-item">
                                <div class="document-info">
                                    <span class="document-icon">PDF</span>
                                    <div class="document-details">
                                        <span class="document-name">Company Profile / Brochure</span>
                                        <span class="document-meta">
                                            <c:choose>
                                                <c:when test="${not empty supplier.profilePath}">
                                                    Uploaded: ${supplier.profileDate}
                                                </c:when>
                                                <c:otherwise>
                                                    Not uploaded
                                                </c:otherwise>
                                            </c:choose>
                                        </span>
                                    </div>
                                </div>
                                <div class="document-actions">
                                    <c:choose>
                                        <c:when test="${not empty supplier.profilePath}">
                                            <a href="${pageContext.request.contextPath}/supplier/download-document?type=profile" class="btn-icon">View</a>
                                        </c:when>
                                        <c:otherwise>
                                            <button type="button" class="btn btn-outline" onclick="showUploadModal('profile')">Upload</button>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Security Tab -->
                <div id="security" class="tab-content">
                    <div class="profile-card">
                        <div class="card-header">
                            <h2>Change Password</h2>
                        </div>

                        <form action="${pageContext.request.contextPath}/supplier/profile" method="POST">
                            <input type="hidden" name="action" value="changePassword">

                            <div class="form-group">
                                <label for="currentPassword">Current Password <span class="required">*</span></label>
                                <input type="password" id="currentPassword" name="currentPassword" required>
                            </div>

                            <div class="form-group">
                                <label for="newPassword">New Password <span class="required">*</span></label>
                                <input type="password" id="newPassword" name="newPassword" required minlength="8">
                                <small>Minimum 8 characters, include uppercase, lowercase, and number</small>
                            </div>

                            <div class="form-group">
                                <label for="confirmPassword">Confirm New Password <span class="required">*</span></label>
                                <input type="password" id="confirmPassword" name="confirmPassword" required>
                                <div id="passwordMatch" class="match-indicator"></div>
                            </div>

                            <div class="form-actions">
                                <button type="submit" class="btn btn-primary">Change Password</button>
                            </div>
                        </form>
                    </div>

                    <div class="profile-card">
                        <div class="card-header">
                            <h2>Account Information</h2>
                        </div>

                        <div class="info-grid">
                            <div class="info-item">
                                <span class="info-label">Account Created</span>
                                <span class="info-value">${supplier.formattedCreatedAt}</span>
                            </div>
                            <div class="info-item">
                                <span class="info-label">Last Login</span>
                                <span class="info-value">${supplier.formattedLastLogin}</span>
                            </div>
                            <div class="info-item">
                                <span class="info-label">Account Status</span>
                                <span class="info-value status-active">Active</span>
                            </div>
                        </div>
                    </div>
                </div>

            </main>
        </div>

        <!-- Upload Modal -->
        <div id="uploadModal" class="modal">
            <div class="modal-content">
                <div class="modal-header">
                    <h3>Upload Document</h3>
                    <button class="modal-close" onclick="closeUploadModal()">&times;</button>
                </div>
                <form action="${pageContext.request.contextPath}/supplier/profile" method="POST" enctype="multipart/form-data">
                    <input type="hidden" name="action" value="uploadDocument">
                    <input type="hidden" id="documentType" name="documentType">

                    <div class="modal-body">
                        <div class="form-group">
                            <label for="documentFile">Select File (PDF only, max 5MB)</label>
                            <input type="file" id="documentFile" name="documentFile" accept=".pdf,application/pdf" required>
                        </div>
                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" onclick="closeUploadModal()">Cancel</button>
                        <button type="submit" class="btn btn-primary">Upload</button>
                    </div>
                </form>
            </div>
        </div>

        <jsp:include page="/shared/footer.jsp" />

        <script>
            // Tab switching
            document.querySelectorAll('.tab-link').forEach(tab => {
                tab.addEventListener('click', function (e) {
                    e.preventDefault();

                    document.querySelectorAll('.tab-link').forEach(t => t.classList.remove('active'));
                    document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));

                    this.classList.add('active');
                    const tabId = this.getAttribute('data-tab');
                    document.getElementById(tabId).classList.add('active');
                });
            });

            // Check URL hash for tab
            if (window.location.hash) {
                const hash = window.location.hash.substring(1);
                const tab = document.querySelector(`.tab-link[data-tab="${hash}"]`);
                if (tab)
                    tab.click();
            }

            // Enable edit mode
            function enableEdit(formId) {
                const form = document.getElementById(formId);
                const inputs = form.querySelectorAll('input:not([readonly]), textarea');
                const actions = form.querySelector('.form-actions');

                inputs.forEach(input => input.disabled = false);
                if (actions)
                    actions.style.display = 'flex';
            }

            // Cancel edit mode
            function cancelEdit(formId) {
                const form = document.getElementById(formId);
                const inputs = form.querySelectorAll('input:not([readonly]), textarea');
                const actions = form.querySelector('.form-actions');

                inputs.forEach(input => input.disabled = true);
                if (actions)
                    actions.style.display = 'none';
            }

            // Password match validation
            document.getElementById('confirmPassword').addEventListener('input', function () {
                const newPass = document.getElementById('newPassword').value;
                const confirmPass = this.value;
                const indicator = document.getElementById('passwordMatch');

                if (confirmPass === '') {
                    indicator.textContent = '';
                    indicator.className = 'match-indicator';
                } else if (newPass === confirmPass) {
                    indicator.textContent = 'Passwords match';
                    indicator.className = 'match-indicator match';
                } else {
                    indicator.textContent = 'Passwords do not match';
                    indicator.className = 'match-indicator mismatch';
                }
            });

            // Upload modal
            function showUploadModal(type) {
                document.getElementById('documentType').value = type;
                document.getElementById('uploadModal').style.display = 'flex';
            }

            function closeUploadModal() {
                document.getElementById('uploadModal').style.display = 'none';
            }

            window.onclick = function (event) {
                const modal = document.getElementById('uploadModal');
                if (event.target === modal)
                    closeUploadModal();
            };

            // Close alerts
            setTimeout(function () {
                const alerts = document.querySelectorAll('.alert');
                alerts.forEach(function (alert) {
                    alert.style.transition = 'opacity 0.5s';
                    alert.style.opacity = '0';
                    setTimeout(function () {
                        alert.remove();
                    }, 500);
                });
            }, 5000);
        </script>

    </body>
</html>