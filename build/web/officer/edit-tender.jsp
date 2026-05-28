<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Edit Tender - ProcureGov</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ministry-style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/officerdashboard.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/edit-tender.css">
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
                        <a href="${pageContext.request.contextPath}/officer/tenders" class="back-link">
                            Back to Tenders
                        </a>
                        <h1>Edit Tender</h1>
                        <p>Update tender details (only available for draft tenders)</p>
                    </div>
                </div>

                <!-- Success/Error Messages -->
                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-error">
                        <span>!</span>
                        <span>${errorMessage}</span>
                        <button class="close-alert" onclick="this.parentElement.remove()">&times;</button>
                    </div>
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
                        <button class="close-alert" onclick="this.parentElement.remove()">&times;</button>
                    </div>
                </c:if>

                <c:if test="${not empty tender}">

                    <!-- Warning: Only Draft Tenders Can Be Edited -->
                    <c:if test="${tender.status != 'DRAFT'}">
                        <div class="alert alert-warning">
                            <span>!</span>
                            <span>This tender is already published and cannot be edited.</span>
                        </div>
                    </c:if>

                    <!-- Edit Tender Form -->
                    <div class="form-card">
                        <div class="form-card-header">
                            <h2>Tender Details</h2>
                            <p>All fields marked with <span class="required">*</span> are mandatory</p>
                        </div>

                        <form action="${pageContext.request.contextPath}/officer/edit-tender" 
                              method="POST" 
                              enctype="multipart/form-data"
                              id="editTenderForm"
                              onsubmit="return validateTenderForm()">

                            <input type="hidden" name="tenderId" value="${tender.tenderId}">
                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                            <!-- Reference Number (Read-only) -->
                            <div class="form-row">
                                <div class="form-group half-width">
                                    <label for="referenceNumber">Reference Number</label>
                                    <div class="input-wrapper">
                                        <span class="input-icon">#</span>
                                        <input type="text" 
                                               id="referenceNumber" 
                                               value="${tender.referenceNumber}"
                                               readonly
                                               class="readonly-input"
                                               style="background-color: #f8f9fa; cursor: not-allowed;">
                                    </div>
                                    <small class="field-hint">Reference number cannot be changed</small>
                                </div>

                                <!-- Category -->
                                <div class="form-group half-width">
                                    <label for="category">
                                        Category <span class="required">*</span>
                                    </label>
                                    <div class="input-wrapper">
                                        <span class="input-icon">C</span>
                                        <select id="category" name="category" required ${tender.status != 'DRAFT' ? 'disabled' : ''}>
                                            <option value="">-- Select Category --</option>
                                            <option value="CONSTRUCTION" ${tender.category == 'CONSTRUCTION' ? 'selected' : ''}>Construction</option>
                                            <option value="ROADS" ${tender.category == 'ROADS' ? 'selected' : ''}>Roads & Infrastructure</option>
                                            <option value="ELECTRICAL" ${tender.category == 'ELECTRICAL' ? 'selected' : ''}>Electrical Works</option>
                                            <option value="PLUMBING" ${tender.category == 'PLUMBING' ? 'selected' : ''}>Plumbing & Sanitation</option>
                                            <option value="GENERAL_SERVICES" ${tender.category == 'GENERAL_SERVICES' ? 'selected' : ''}>General Services</option>
                                        </select>
                                    </div>
                                    <div class="error-message" id="categoryError"></div>
                                </div>
                            </div>

                            <!-- Tender Title -->
                            <div class="form-row">
                                <div class="form-group full-width">
                                    <label for="title">
                                        Tender Title <span class="required">*</span>
                                    </label>
                                    <div class="input-wrapper">
                                        <span class="input-icon">T</span>
                                        <input type="text" 
                                               id="title" 
                                               name="title" 
                                               value="${tender.title}"
                                               placeholder="e.g., Construction of Maseru District Hospital Access Road"
                                               maxlength="200"
                                               required
                                               ${tender.status != 'DRAFT' ? 'readonly' : ''}>
                                    </div>
                                    <small class="field-hint">Clear, descriptive title of the procurement requirement</small>
                                    <div class="error-message" id="titleError"></div>
                                </div>
                            </div>

                            <!-- Description -->
                            <div class="form-row">
                                <div class="form-group full-width">
                                    <label for="description">
                                        Description <span class="required">*</span>
                                    </label>
                                    <div class="input-wrapper">
                                        <span class="input-icon">D</span>
                                        <textarea id="description" 
                                                  name="description" 
                                                  rows="5"
                                                  placeholder="Provide detailed description of the works, goods, or services required..."
                                                  required
                                                  ${tender.status != 'DRAFT' ? 'readonly' : ''}>${tender.description}</textarea>
                                    </div>
                                    <small class="field-hint">
                                        Include scope of work, specifications, and any special requirements
                                    </small>
                                    <div class="error-message" id="descriptionError"></div>
                                </div>
                            </div>

                            <!-- Estimated Value -->
                            <div class="form-row">
                                <div class="form-group half-width">
                                    <label for="estimatedValue">
                                        Estimated Value (Maloti) <span class="required">*</span>
                                    </label>
                                    <div class="input-wrapper">
                                        <span class="input-icon">M</span>
                                        <input type="number" 
                                               id="estimatedValue" 
                                               name="estimatedValue" 
                                               value="${tender.estimatedValue}"
                                               placeholder="e.g., 2500000.00"
                                               min="0.01"
                                               step="0.01"
                                               required
                                               ${tender.status != 'DRAFT' ? 'readonly' : ''}>
                                    </div>
                                    <small class="field-hint">Estimated budget for this procurement</small>
                                    <div class="error-message" id="estimatedValueError"></div>
                                </div>
                            </div>

                            <!-- Closing Date and Time -->
                            <div class="form-row">
                                <div class="form-group half-width">
                                    <label for="closingDate">
                                        Closing Date <span class="required">*</span>
                                    </label>
                                    <div class="input-wrapper">
                                        <span class="input-icon">D</span>
                                        <input type="date" 
                                               id="closingDate" 
                                               name="closingDate" 
                                               value="${tender.closingDateTime.toLocalDate()}"
                                               min="${minDate}"
                                               required
                                               ${tender.status != 'DRAFT' ? 'readonly' : ''}>
                                    </div>
                                    <div class="error-message" id="closingDateError"></div>
                                </div>

                                <div class="form-group half-width">
                                    <label for="closingTime">
                                        Closing Time <span class="required">*</span>
                                    </label>
                                    <div class="input-wrapper">
                                        <span class="input-icon">T</span>
                                        <input type="time" 
                                               id="closingTime" 
                                               name="closingTime" 
                                               value="${tender.closingDateTime.toLocalTime()}"
                                               required
                                               ${tender.status != 'DRAFT' ? 'readonly' : ''}>
                                    </div>
                                    <small class="field-hint">Bids accepted until this exact time (Lesotho time)</small>
                                    <div class="error-message" id="closingTimeError"></div>
                                </div>
                            </div>

                            <!-- Preview of combined datetime -->
                            <div class="form-row">
                                <div class="form-group full-width">
                                    <div class="datetime-preview" id="datetimePreview">
                                        <span id="previewText">${tender.formattedClosingDateTime}</span>
                                    </div>
                                </div>
                            </div>

                            <!-- Current Tender Document -->
                            <div class="form-row">
                                <div class="form-group full-width">
                                    <label>Current Tender Notice</label>
                                    <div class="current-file">
                                        <span class="file-icon">PDF</span>
                                        <span class="file-name">${tender.tenderNoticeFileName}</span>
                                        <a href="${pageContext.request.contextPath}/download?file=${tender.tenderNoticePath}" 
                                           class="btn btn-outline btn-sm" target="_blank">
                                            View Current Document
                                        </a>
                                    </div>
                                    <small class="field-hint">Upload a new file only if you want to replace the current document</small>
                                </div>
                            </div>

                            <!-- New Tender Document Upload (Optional) -->
                            <c:if test="${tender.status == 'DRAFT'}">
                                <div class="form-row">
                                    <div class="form-group full-width">
                                        <label for="tenderDocument">
                                            Replace Tender Notice (Optional)
                                        </label>

                                        <div class="file-upload-area" id="fileUploadArea">
                                            <div class="upload-icon">U</div>
                                            <p class="upload-text">
                                                Drag and drop your PDF file here, or click to browse
                                            </p>
                                            <p class="upload-hint">
                                                Maximum file size: 5MB | Allowed format: PDF only
                                            </p>
                                            <input type="file" 
                                                   id="tenderDocument" 
                                                   name="tenderDocument" 
                                                   accept=".pdf,application/pdf"
                                                   style="display: none;">
                                            <button type="button" 
                                                    class="btn btn-outline" 
                                                    onclick="document.getElementById('tenderDocument').click()">
                                                Browse Files
                                            </button>
                                        </div>

                                        <!-- Selected File Display -->
                                        <div class="selected-file" id="selectedFileDisplay" style="display: none;">
                                            <span class="file-name" id="selectedFileName"></span>
                                            <span class="file-size" id="selectedFileSize"></span>
                                            <button type="button" 
                                                    class="remove-file" 
                                                    onclick="clearFileSelection()">
                                                X
                                            </button>
                                        </div>

                                        <div class="error-message" id="tenderDocumentError"></div>
                                    </div>
                                </div>
                            </c:if>

                            <!-- Form Actions -->
                            <c:if test="${tender.status == 'DRAFT'}">
                                <div class="form-actions">
                                    <button type="submit" class="btn btn-primary btn-lg" id="submitBtn">
                                        Save Changes
                                    </button>

                                    <a href="${pageContext.request.contextPath}/officer/tenders" 
                                       class="btn btn-secondary btn-lg">
                                        Cancel
                                    </a>
                                </div>
                            </c:if>

                            <c:if test="${tender.status != 'DRAFT'}">
                                <div class="form-actions">
                                    <a href="${pageContext.request.contextPath}/officer/tenders" 
                                       class="btn btn-primary btn-lg">
                                        Back to Tenders
                                    </a>
                                </div>
                            </c:if>

                        </form>
                    </div>

                </c:if>

                <c:if test="${empty tender}">
                    <div class="error-card">
                        <div class="error-icon">!</div>
                        <h3>Tender Not Found</h3>
                        <p>The tender you are trying to edit could not be found.</p>
                        <a href="${pageContext.request.contextPath}/officer/tenders" class="btn btn-primary">
                            Back to Tenders
                        </a>
                    </div>
                </c:if>

            </main>
        </div>

        <jsp:include page="/shared/footer.jsp" />

        <script>
            // File upload handling
            const fileInput = document.getElementById('tenderDocument');
            const fileUploadArea = document.getElementById('fileUploadArea');
            const selectedFileDisplay = document.getElementById('selectedFileDisplay');
            const selectedFileName = document.getElementById('selectedFileName');
            const selectedFileSize = document.getElementById('selectedFileSize');

            if (fileUploadArea) {
                fileUploadArea.addEventListener('dragover', (e) => {
                    e.preventDefault();
                    fileUploadArea.classList.add('dragover');
                });

                fileUploadArea.addEventListener('dragleave', () => {
                    fileUploadArea.classList.remove('dragover');
                });

                fileUploadArea.addEventListener('drop', (e) => {
                    e.preventDefault();
                    fileUploadArea.classList.remove('dragover');
                    const files = e.dataTransfer.files;
                    if (files.length > 0) {
                        handleFileSelection(files[0]);
                    }
                });
            }

            if (fileInput) {
                fileInput.addEventListener('change', (e) => {
                    if (fileInput.files.length > 0) {
                        handleFileSelection(fileInput.files[0]);
                    }
                });
            }

            function handleFileSelection(file) {
                const allowedExtensions = ['.pdf'];
                const fileName = file.name.toLowerCase();
                const isValidExtension = allowedExtensions.some(ext => fileName.endsWith(ext));

                if (!isValidExtension) {
                    showError('tenderDocument', 'Only PDF files are allowed');
                    return;
                }

                const maxSize = 5 * 1024 * 1024;
                if (file.size > maxSize) {
                    showError('tenderDocument', 'File size exceeds 5MB limit');
                    return;
                }

                selectedFileName.textContent = file.name;
                selectedFileSize.textContent = formatFileSize(file.size);
                selectedFileDisplay.style.display = 'flex';
                fileUploadArea.style.display = 'none';
                clearError('tenderDocument');
            }

            function formatFileSize(bytes) {
                if (bytes < 1024)
                    return bytes + ' B';
                if (bytes < 1024 * 1024)
                    return (bytes / 1024).toFixed(2) + ' KB';
                return (bytes / (1024 * 1024)).toFixed(2) + ' MB';
            }

            function clearFileSelection() {
                fileInput.value = '';
                selectedFileDisplay.style.display = 'none';
                fileUploadArea.style.display = 'block';
            }

            function validateTenderForm() {
                let isValid = true;

                const title = document.getElementById('title').value.trim();
                if (title.length < 10) {
                    showError('title', 'Title must be at least 10 characters');
                    isValid = false;
                } else {
                    clearError('title');
                }

                const category = document.getElementById('category').value;
                if (!category) {
                    showError('category', 'Please select a category');
                    isValid = false;
                } else {
                    clearError('category');
                }

                const description = document.getElementById('description').value.trim();
                if (description.length < 50) {
                    showError('description', 'Description must be at least 50 characters');
                    isValid = false;
                } else {
                    clearError('description');
                }

                const estimatedValue = parseFloat(document.getElementById('estimatedValue').value);
                if (isNaN(estimatedValue) || estimatedValue <= 0) {
                    showError('estimatedValue', 'Please enter a valid amount greater than 0');
                    isValid = false;
                } else {
                    clearError('estimatedValue');
                }

                const closingDate = document.getElementById('closingDate').value;
                if (!closingDate) {
                    showError('closingDate', 'Please select a closing date');
                    isValid = false;
                } else {
                    clearError('closingDate');
                }

                const closingTime = document.getElementById('closingTime').value;
                if (!closingTime) {
                    showError('closingTime', 'Please select a closing time');
                    isValid = false;
                } else {
                    clearError('closingTime');
                }

                if (isValid) {
                    document.getElementById('submitBtn').disabled = true;
                    document.getElementById('submitBtn').innerHTML = 'Saving...';
                }

                return isValid;
            }

            function showError(fieldId, message) {
                const errorDiv = document.getElementById(fieldId + 'Error');
                if (errorDiv) {
                    errorDiv.textContent = message;
                    errorDiv.style.display = 'block';
                }
                document.getElementById(fieldId)?.classList.add('input-error');
            }

            function clearError(fieldId) {
                const errorDiv = document.getElementById(fieldId + 'Error');
                if (errorDiv) {
                    errorDiv.textContent = '';
                    errorDiv.style.display = 'none';
                }
                document.getElementById(fieldId)?.classList.remove('input-error');
            }

            // Close alerts after 5 seconds
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