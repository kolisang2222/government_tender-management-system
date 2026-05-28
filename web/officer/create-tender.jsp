<%-- 
    Document   : create-tender
    Created on : Apr 14, 2026, 11:26:14 AM
    Author     : kolisang
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ministry-style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/officerdashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/create_tender.css">
    <link rel="stylesheet" href="procgov-dashboard.css">
    <title>Create Tender - ProcureGov | Ministry of Public Works</title>
</head>
<body class="dashboard-body">

    <!-- Navigation Bar -->
    <jsp:include page="/shared/navigation.jsp" />

    <!-- ============================================================ -->
    <!-- MAIN LAYOUT WITH SIDEBAR                                      -->
    <!-- ============================================================ -->
    <div class="dashboard-layout">

        <!-- Sidebar Navigation -->
        <jsp:include page="/shared/sidebar.jsp" />

        <!-- Main Content Area -->
        <main class="dashboard-main">

            <!-- Page Header -->
            <div class="page-header">
                <h1>Create New Tender</h1>
                <p class="header-description">
                    Publish a new government tender for suppliers to bid on
                </p>
            </div>

            <!-- ======================================================== -->
            <!-- ERROR/SUCCESS MESSAGES                                    -->
            <!-- ======================================================== -->

            <c:if test="${not empty errorMessage}">
                <div class="alert alert-error">
                    <span class="alert-icon">!</span>
                    <span>${errorMessage}</span>
                    <button class="close-alert" onclick="this.parentElement.remove()">&times;</button>
                </div>
            </c:if>

            <c:if test="${not empty successMessage}">
                <div class="alert alert-success">
                    <span class="alert-icon">OK</span>
                    <span>${successMessage}</span>
                    <button class="close-alert" onclick="this.parentElement.remove()">&times;</button>
                </div>
            </c:if>

            <!-- Validation Error Summary -->
            <c:if test="${not empty validationErrors}">
                <div class="alert alert-warning">
                    <span class="alert-icon">!</span>
                    <div>
                        <strong>Please correct the following errors:</strong>
                        <ul style="margin-top: 0.5rem; padding-left: 1.5rem;">
                            <c:forEach var="error" items="${validationErrors}">
                                <li>${error}</li>
                            </c:forEach>
                        </ul>
                    </div>
                </div>
            </c:if>

            <!-- ======================================================== -->
            <!-- CREATE TENDER FORM                                        -->
            <!-- ======================================================== -->

            <div class="form-card">
                <div class="form-card-header">
                    <h2>Tender Details</h2>
                    <p>All fields marked with <span class="required">*</span> are mandatory</p>
                </div>

                <form action="${pageContext.request.contextPath}/officer/create-tender" 
                      method="POST" 
                      enctype="multipart/form-data"
                      id="createTenderForm"
                      onsubmit="return validateTenderForm()">

                    <!-- Hidden field for CSRF protection -->
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                    <!-- ================================================ -->
                    <!-- SECTION 1: BASIC INFORMATION                     -->
                    <!-- ================================================ -->

                    <fieldset class="form-section">
                        <legend>Basic Information</legend>

                        <!-- Tender Title -->
                        <div class="form-row">
                            <div class="form-group full-width">
                                <label for="title">
                                    Tender Title <span class="required">*</span>
                                </label>
                                <div class="input-wrapper">
                                    <input type="text" 
                                           id="title" 
                                           name="title" 
                                           value="${tender.title}"
                                           placeholder="e.g., Construction of Maseru District Hospital Access Road"
                                           maxlength="200"
                                           required>
                                </div>
                                <small class="field-hint">Clear, descriptive title of the procurement requirement</small>
                                <div class="error-message" id="titleError"></div>
                            </div>
                        </div>

                        <!-- Reference Number (Auto-generated, Read-only) -->
                        <div class="form-row">
                            <div class="form-group half-width">
                                <label for="referenceNumber">
                                    Reference Number
                                </label>
                                <div class="input-wrapper">
                                    <input type="text" 
                                           id="referenceNumber" 
                                           name="referenceNumber" 
                                           value="${generatedReference}"
                                           readonly
                                           class="readonly-input"
                                           style="background-color: #f8f9fa; cursor: not-allowed;">
                                </div>
                                <small class="field-hint">
                                    Auto-generated in format: MPW-YYYY-NNNN
                                    <c:if test="${empty generatedReference}">
                                        (Will be generated upon submission)
                                    </c:if>
                                </small>
                            </div>

                            <!-- Category -->
                            <div class="form-group half-width">
                                <label for="category">
                                    Category <span class="required">*</span>
                                </label>
                                <div class="input-wrapper">
                                    <select id="category" name="category" required>
                                        <option value="">-- Select Category --</option>
                                        <option value="CONSTRUCTION" ${tender.category == 'CONSTRUCTION' ? 'selected' : ''}>
                                            Construction
                                        </option>
                                        <option value="ROADS" ${tender.category == 'ROADS' ? 'selected' : ''}>
                                            Roads & Infrastructure
                                        </option>
                                        <option value="ELECTRICAL" ${tender.category == 'ELECTRICAL' ? 'selected' : ''}>
                                            Electrical Works
                                        </option>
                                        <option value="PLUMBING" ${tender.category == 'PLUMBING' ? 'selected' : ''}>
                                            Plumbing & Sanitation
                                        </option>
                                        <option value="GENERAL_SERVICES" ${tender.category == 'GENERAL_SERVICES' ? 'selected' : ''}>
                                            General Services
                                        </option>
                                    </select>
                                </div>
                                <div class="error-message" id="categoryError"></div>
                            </div>
                        </div>

                        <!-- Description -->
                        <div class="form-row">
                            <div class="form-group full-width">
                                <label for="description">
                                    Description <span class="required">*</span>
                                </label>
                                <div class="input-wrapper">
                                    <textarea id="description" 
                                              name="description" 
                                              rows="5"
                                              placeholder="Provide detailed description of the works, goods, or services required..."
                                              required>${tender.description}</textarea>
                                </div>
                                <small class="field-hint">
                                    Include scope of work, specifications, and any special requirements
                                </small>
                                <div class="error-message" id="descriptionError"></div>
                            </div>
                        </div>
                    </fieldset>

                    <!-- ================================================ -->
                    <!-- SECTION 2: FINANCIAL INFORMATION                 -->
                    <!-- ================================================ -->

                    <fieldset class="form-section">
                        <legend>Financial Information</legend>

                        <!-- Estimated Value -->
                        <div class="form-row">
                            <div class="form-group half-width">
                                <label for="estimatedValue">
                                    Estimated Value (Maloti) <span class="required">*</span>
                                </label>
                                <div class="input-wrapper">
                                    <span class="currency-prefix">M</span>
                                    <input type="number" 
                                           id="estimatedValue" 
                                           name="estimatedValue" 
                                           value="${tender.estimatedValue}"
                                           placeholder="e.g., 2500000.00"
                                           min="0.01"
                                           step="0.01"
                                           required>
                                </div>
                                <small class="field-hint">Estimated budget for this procurement</small>
                                <div class="error-message" id="estimatedValueError"></div>
                            </div>
                        </div>
                    </fieldset>

                    <!-- ================================================ -->
                    <!-- SECTION 3: SUBMISSION DEADLINE                    -->
                    <!-- ================================================ -->

                    <fieldset class="form-section">
                        <legend>Submission Deadline</legend>

                        <div class="form-row">
                            <!-- Closing Date -->
                            <div class="form-group half-width">
                                <label for="closingDate">
                                    Closing Date <span class="required">*</span>
                                </label>
                                <div class="input-wrapper">
                                    <input type="date" 
                                           id="closingDate" 
                                           name="closingDate" 
                                           value="${param.closingDate}"
                                           min="${minDate}"
                                           required>
                                </div>
                                <div class="error-message" id="closingDateError"></div>
                            </div>

                            <!-- Closing Time -->
                            <div class="form-group half-width">
                                <label for="closingTime">
                                    Closing Time <span class="required">*</span>
                                </label>
                                <div class="input-wrapper">
                                    <input type="time" 
                                           id="closingTime" 
                                           name="closingTime" 
                                           value="${param.closingTime != null ? param.closingTime : '12:00'}"
                                           required>
                                </div>
                                <small class="field-hint">Bids accepted until this exact time (Lesotho time)</small>
                                <div class="error-message" id="closingTimeError"></div>
                            </div>
                        </div>

                        <!-- Preview of combined datetime -->
                        <div class="form-row">
                            <div class="form-group full-width">
                                <div class="datetime-preview" id="datetimePreview">
                                    <span id="previewText">Select date and time to preview</span>
                                </div>
                            </div>
                        </div>
                    </fieldset>

                    <!-- ================================================ -->
                    <!-- SECTION 4: TENDER DOCUMENT UPLOAD                 -->
                    <!-- ================================================ -->

                    <fieldset class="form-section">
                        <legend>Tender Notice Document</legend>

                        <div class="form-row">
                            <div class="form-group full-width">
                                <label for="tenderDocument">
                                    Upload Tender Notice (PDF) <span class="required">*</span>
                                </label>

                                <!-- File Upload Area -->
                                <div class="file-upload-area" id="fileUploadArea">
                                    <p class="file-upload-text">
                                        Drag and drop your PDF file here, or click to browse
                                    </p>
                                    <p class="file-upload-hint">
                                        Maximum file size: 5MB | Allowed format: PDF only
                                    </p>
                                    <input type="file" 
                                           id="tenderDocument" 
                                           name="tenderDocument" 
                                           accept=".pdf,application/pdf"
                                           required
                                           style="display: none;">
                                    <button type="button" 
                                            class="btn btn-secondary" 
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
                    </fieldset>

                    <!-- ================================================ -->
                    <!-- SECTION 5: ADDITIONAL SETTINGS                   -->
                    <!-- ================================================ -->

                    <fieldset class="form-section">
                        <legend>Additional Settings</legend>

                        <div class="form-row">
                            <div class="form-group full-width">
                                <label class="checkbox-label">
                                    <input type="checkbox" name="publishImmediately" value="true" checked>
                                    <span class="checkbox-text">
                                        <strong>Publish immediately</strong>
                                        <br>
                                        <small>If checked, tender will be visible to suppliers immediately after creation. 
                                            If unchecked, tender will be saved as Draft.</small>
                                    </span>
                                </label>
                            </div>
                        </div>

                        <div class="form-row">
                            <div class="form-group full-width">
                                <label class="checkbox-label">
                                    <input type="checkbox" name="notifySuppliers" value="true">
                                    <span class="checkbox-text">
                                        <strong>Notify registered suppliers</strong>
                                        <br>
                                        <small>Send email notification to all registered suppliers about this new tender</small>
                                    </span>
                                </label>
                            </div>
                        </div>
                    </fieldset>

                    <!-- ================================================ -->
                    <!-- FORM ACTIONS                                      -->
                    <!-- ================================================ -->

                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary btn-lg" id="submitBtn">
                            <span class="btn-text">Create Tender</span>
                        </button>

                        <button type="button" 
                                class="btn btn-outline-secondary btn-lg"
                                onclick="saveAsDraft()">
                            Save as Draft
                        </button>

                        <a href="${pageContext.request.contextPath}/officer/dashboard.jsp" 
                           class="btn btn-secondary btn-lg">
                            Cancel
                        </a>
                    </div>

                </form>
            </div><!-- End Form Card -->

            <!-- ======================================================== -->
            <!-- HELPFUL INFORMATION PANEL                                 -->
            <!-- ======================================================== -->

            <div class="info-panel">
                <h3>Important Notes</h3>
                <ul>
                    <li><strong>Reference Number:</strong> Automatically generated in format MPW-YYYY-NNNN</li>
                    <li><strong>Draft Status:</strong> Uncheck "Publish immediately" to save as draft for later editing</li>
                    <li><strong>Closing Date:</strong> Must be at least 7 days in the future</li>
                    <li><strong>Tender Notice:</strong> PDF format only, maximum 5MB</li>
                    <li><strong>Editing:</strong> Tenders can only be edited while in Draft status</li>
                </ul>
            </div>

        </main><!-- End Main Content -->

    </div><!-- End Dashboard Layout -->

    <!-- Footer -->
    <jsp:include page="/shared/footer.jsp" />

    <!-- ============================================================ -->
    <!-- JAVASCRIPT FOR FORM VALIDATION                                -->
    <!-- ============================================================ -->

    <script>
        // Set minimum date (today + 2 minutes for testing)
        const today = new Date();
        const minDate = new Date(today);
        minDate.setMinutes(today.getMinutes() + 2);

        const minDateString = minDate.toISOString().split('T')[0];
        document.getElementById('closingDate').setAttribute('min', minDateString);

        // Set default date (30 minutes from now for testing)
        const defaultDate = new Date(today);
        defaultDate.setMinutes(today.getMinutes() + 1);
        const defaultDateString = defaultDate.toISOString().split('T')[0];
        const defaultTimeString = defaultDate.toTimeString().substring(0, 5);

        if (!document.getElementById('closingDate').value) {
            document.getElementById('closingDate').value = defaultDateString;
        }
        if (!document.getElementById('closingTime').value) {
            document.getElementById('closingTime').value = defaultTimeString;
        }

        // Update datetime preview
        function updateDateTimePreview() {
            const dateInput = document.getElementById('closingDate');
            const timeInput = document.getElementById('closingTime');
            const previewText = document.getElementById('previewText');

            if (dateInput.value && timeInput.value) {
                const date = new Date(dateInput.value + 'T' + timeInput.value);
                const options = {
                    weekday: 'long',
                    year: 'numeric',
                    month: 'long',
                    day: 'numeric',
                    hour: '2-digit',
                    minute: '2-digit'
                };
                previewText.textContent = 'Closing: ' + date.toLocaleString('en-GB', options) + ' (Lesotho Time)';
            } else {
                previewText.textContent = 'Select date and time to preview';
            }
        }

        document.getElementById('closingDate').addEventListener('change', updateDateTimePreview);
        document.getElementById('closingTime').addEventListener('change', updateDateTimePreview);

        // Initialize preview
        updateDateTimePreview();

        // File upload handling
        const fileInput = document.getElementById('tenderDocument');
        const fileUploadArea = document.getElementById('fileUploadArea');
        const selectedFileDisplay = document.getElementById('selectedFileDisplay');
        const selectedFileName = document.getElementById('selectedFileName');
        const selectedFileSize = document.getElementById('selectedFileSize');

        // Drag and drop
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

        fileUploadArea.addEventListener('click', () => {
            fileInput.click();
        });

        fileInput.addEventListener('change', (e) => {
            if (fileInput.files.length > 0) {
                handleFileSelection(fileInput.files[0]);
            }
        });

        function handleFileSelection(file) {
            // Validate file type
            if (!file.name.toLowerCase().endsWith('.pdf')) {
                showError('tenderDocument', 'Only PDF files are allowed');
                return;
            }

            // Validate file size (5MB)
            if (file.size > 5 * 1024 * 1024) {
                showError('tenderDocument', 'File size exceeds 5MB limit');
                return;
            }

            // Display selected file
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

        // Form validation
        function validateTenderForm() {
            let isValid = true;

            // Validate title
            const title = document.getElementById('title').value.trim();
            if (title.length < 10) {
                showError('title', 'Title must be at least 10 characters');
                isValid = false;
            } else {
                clearError('title');
            }

            // Validate category
            const category = document.getElementById('category').value;
            if (!category) {
                showError('category', 'Please select a category');
                isValid = false;
            } else {
                clearError('category');
            }

            // Validate description
            const description = document.getElementById('description').value.trim();
            if (description.length < 50) {
                showError('description', 'Description must be at least 50 characters');
                isValid = false;
            } else {
                clearError('description');
            }

            // Validate estimated value
            const estimatedValue = parseFloat(document.getElementById('estimatedValue').value);
            if (isNaN(estimatedValue) || estimatedValue <= 0) {
                showError('estimatedValue', 'Please enter a valid amount greater than 0');
                isValid = false;
            } else {
                clearError('estimatedValue');
            }

            // Validate closing date
            const closingDate = document.getElementById('closingDate').value;
            if (!closingDate) {
                showError('closingDate', 'Please select a closing date');
                isValid = false;
            } else {
                const selectedDate = new Date(closingDate + 'T' + document.getElementById('closingTime').value);
                if (selectedDate < minDate) {
                    showError('closingDate', 'Closing date must be at least 30 minutes from now');
                    isValid = false;
                } else {
                    clearError('closingDate');
                }
            }

            // Validate closing time
            const closingTime = document.getElementById('closingTime').value;
            if (!closingTime) {
                showError('closingTime', 'Please select a closing time');
                isValid = false;
            } else {
                clearError('closingTime');
            }

            // Validate file upload
            if (fileInput.files.length === 0) {
                showError('tenderDocument', 'Please upload a tender notice document');
                isValid = false;
            } else {
                clearError('tenderDocument');
            }

            // Disable submit button to prevent double submission
            if (isValid) {
                document.getElementById('submitBtn').disabled = true;
                document.getElementById('submitBtn').innerHTML = '<span>Creating Tender...</span>';
            }

            return isValid;
        }

        function saveAsDraft() {
            // Uncheck publish checkbox
            document.querySelector('input[name="publishImmediately"]').checked = false;
            // Submit the form
            document.getElementById('createTenderForm').submit();
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

        // Character counters
        document.getElementById('title').addEventListener('input', function () {
            const remaining = 200 - this.value.length;
            const hint = this.parentElement.parentElement.querySelector('.field-hint');
            if (remaining < 20) {
                hint.innerHTML = 'Clear, descriptive title (' + remaining + ' characters remaining)';
            }
        });

        document.getElementById('description').addEventListener('input', function() {
            const length = this.value.length;
            const hint = this.parentElement.parentElement.querySelector('.field-hint');
            if (length < 50) {
                hint.innerHTML = 'Description (' + length + '/50 minimum characters)';
            }
        });
    </script>

</body>
</html>