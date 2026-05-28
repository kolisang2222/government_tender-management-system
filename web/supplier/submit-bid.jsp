<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Submit Bid - ProcureGov</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ministry-style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/supplierdashboard.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/submit-bid.css">
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
                        <a href="${pageContext.request.contextPath}/supplier/tender-detail?id=${tender.tenderId}" class="back-link">
                            Back to Tender Details
                        </a>
                        <h1>Submit Bid</h1>
                        <p>Complete the form below to submit your sealed bid</p>
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

                    <!-- Tender Summary Card -->
                    <div class="tender-summary-card">
                        <div class="summary-header">
                            <span class="ref-number">${tender.referenceNumber}</span>
                            <h2>${tender.title}</h2>
                        </div>
                        <div class="summary-details">
                            <div class="detail-item">
                                <span class="detail-label">Category</span>
                                <span class="detail-value">${tender.category.displayName}</span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">Estimated Value</span>
                                <span class="detail-value">${tender.formattedEstimatedValue}</span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">Closing Date</span>
                                <span class="detail-value">${tender.formattedClosingDateTime}</span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">Time Remaining</span>
                                <span class="detail-value ${tender.urgent ? 'urgent' : ''}">${tender.timeRemaining}</span>
                            </div>
                        </div>
                    </div>

                    <!-- Important Notes -->
                    <div class="info-box">
                        <div class="info-icon">i</div>
                        <div class="info-content">
                            <strong>Important Information</strong>
                            <ul>
                                <li>Your bid is sealed and cannot be viewed by other suppliers.</li>
                                <li>You may only submit ONE bid per tender.</li>
                                <li>Once submitted, your bid cannot be edited (only withdrawn before closing).</li>
                                <li>All fields marked with <span class="required">*</span> are mandatory.</li>
                                <li>Maximum file size for supporting documents: 10MB (PDF or DOCX only).</li>
                            </ul>
                        </div>
                    </div>

                    <!-- Bid Submission Form -->
                    <div class="form-card">
                        <div class="form-card-header">
                            <h3>Bid Details</h3>
                        </div>

                        <form action="${pageContext.request.contextPath}/supplier/submit-bid" 
                              method="POST" 
                              enctype="multipart/form-data"
                              id="bidForm"
                              onsubmit="return validateBidForm()">

                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                            <input type="hidden" name="tenderId" value="${tender.tenderId}">
                            <input type="hidden" name="supplierId" value="${sessionScope.userId}">

                            <!-- Bid Amount -->
                            <div class="form-group">
                                <label for="bidAmount">
                                    Bid Amount (Maloti) <span class="required">*</span>
                                </label>
                                <div class="input-wrapper">
                                    <span class="currency-symbol">M</span>
                                    <input type="number" 
                                           id="bidAmount" 
                                           name="bidAmount" 
                                           value="${param.bidAmount}"
                                           placeholder="e.g., 2450000.00"
                                           min="0.01"
                                           step="0.01"
                                           required>
                                </div>
                                <small class="field-hint">Enter your proposed price for this tender</small>
                                <div class="error-message" id="bidAmountError"></div>
                            </div>

                            <!-- Proposed Timeline -->
                            <div class="form-group">
                                <label for="timelineDays">
                                    Proposed Delivery Timeline (Days) <span class="required">*</span>
                                </label>
                                <div class="input-wrapper">
                                    <input type="number" 
                                           id="timelineDays" 
                                           name="timelineDays" 
                                           value="${param.timelineDays}"
                                           placeholder="e.g., 90"
                                           min="1"
                                           max="1095"
                                           required>
                                </div>
                                <small class="field-hint">Number of calendar days to complete the work/delivery</small>
                                <div class="error-message" id="timelineDaysError"></div>
                            </div>

                            <!-- Technical Compliance Statement -->
                            <div class="form-group">
                                <label for="technicalStatement">
                                    Technical Compliance Statement <span class="required">*</span>
                                </label>
                                <textarea id="technicalStatement" 
                                          name="technicalStatement" 
                                          rows="6"
                                          placeholder="Describe how your bid meets the technical requirements and specifications..."
                                          maxlength="600"
                                          required>${param.technicalStatement}</textarea>
                                <div class="character-count">
                                    <span id="charCount">0</span> / 600 characters
                                </div>
                                <small class="field-hint">Explain your technical approach, methodology, and compliance with specifications</small>
                                <div class="error-message" id="technicalStatementError"></div>
                            </div>

                            <!-- Supporting Document Upload -->
                            <div class="form-group">
                                <label for="supportingDocument">
                                    Supporting Document (Optional)
                                </label>

                                <div class="file-upload-area" id="fileUploadArea">
                                    <div class="upload-icon">U</div>
                                    <p class="upload-text">
                                        Drag and drop your file here, or click to browse
                                    </p>
                                    <p class="upload-hint">
                                        Allowed formats: PDF, DOCX | Maximum size: 10MB
                                    </p>
                                    <input type="file" 
                                           id="supportingDocument" 
                                           name="supportingDocument" 
                                           accept=".pdf,.docx,.doc,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                                           style="display: none;">
                                    <button type="button" 
                                            class="btn btn-outline" 
                                            onclick="document.getElementById('supportingDocument').click()">
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

                                <div class="error-message" id="supportingDocumentError"></div>
                            </div>

                            <!-- Declaration -->
                            <div class="form-group declaration-group">
                                <label class="checkbox-label">
                                    <input type="checkbox" id="declaration" name="declaration" required>
                                    <span class="checkbox-text">
                                        I hereby declare that the information provided in this bid is true, 
                                        accurate, and complete. I understand that any false or misleading 
                                        information may result in disqualification.
                                    </span>
                                </label>
                            </div>

                            <div class="form-group declaration-group">
                                <label class="checkbox-label">
                                    <input type="checkbox" id="termsAgreement" name="termsAgreement" required>
                                    <span class="checkbox-text">
                                        I have read, understood, and agree to the terms and conditions 
                                        outlined in the tender notice document.
                                    </span>
                                </label>
                            </div>

                            <!-- Form Actions -->
                            <div class="form-actions">
                                <button type="submit" class="btn btn-primary btn-lg" id="submitBtn">
                                    Submit Sealed Bid
                                </button>

                                <a href="${pageContext.request.contextPath}/supplier/tender-detail?id=${tender.tenderId}" 
                                   class="btn btn-secondary btn-lg">
                                    Cancel
                                </a>
                            </div>

                        </form>
                    </div>

                </c:if>

                <c:if test="${empty tender}">
                    <div class="error-card">
                        <div class="error-icon">!</div>
                        <h3>Tender Not Found</h3>
                        <p>The tender you are trying to bid on could not be found.</p>
                        <a href="${pageContext.request.contextPath}/supplier/tenders" class="btn btn-primary">
                            Browse Tenders
                        </a>
                    </div>
                </c:if>

            </main>
        </div>

        <jsp:include page="/shared/footer.jsp" />

        <script>
            // Character counter for technical statement
            const textarea = document.getElementById('technicalStatement');
            const charCount = document.getElementById('charCount');

            function updateCharCount() {
                const length = textarea.value.length;
                charCount.textContent = length;

                if (length > 600) {
                    charCount.style.color = '#dc3545';
                } else if (length > 500) {
                    charCount.style.color = '#ffc107';
                } else {
                    charCount.style.color = '#6c757d';
                }
            }

            textarea.addEventListener('input', updateCharCount);
            updateCharCount();

            // File upload handling
            const fileInput = document.getElementById('supportingDocument');
            const fileUploadArea = document.getElementById('fileUploadArea');
            const selectedFileDisplay = document.getElementById('selectedFileDisplay');
            const selectedFileName = document.getElementById('selectedFileName');
            const selectedFileSize = document.getElementById('selectedFileSize');

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

            fileInput.addEventListener('change', (e) => {
                if (fileInput.files.length > 0) {
                    handleFileSelection(fileInput.files[0]);
                }
            });

            function handleFileSelection(file) {
                const allowedExtensions = ['.pdf', '.docx', '.doc'];
                const fileName = file.name.toLowerCase();
                const isValidExtension = allowedExtensions.some(ext => fileName.endsWith(ext));

                if (!isValidExtension) {
                    showError('supportingDocument', 'Only PDF and DOCX files are allowed');
                    return;
                }

                const maxSize = 10 * 1024 * 1024;
                if (file.size > maxSize) {
                    showError('supportingDocument', 'File size exceeds 10MB limit');
                    return;
                }

                selectedFileName.textContent = file.name;
                selectedFileSize.textContent = formatFileSize(file.size);
                selectedFileDisplay.style.display = 'flex';
                fileUploadArea.style.display = 'none';
                clearError('supportingDocument');
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
            function validateBidForm() {
                let isValid = true;

                const bidAmount = parseFloat(document.getElementById('bidAmount').value);
                if (isNaN(bidAmount) || bidAmount <= 0) {
                    showError('bidAmount', 'Please enter a valid bid amount greater than zero');
                    isValid = false;
                } else {
                    clearError('bidAmount');
                }

                const timelineDays = parseInt(document.getElementById('timelineDays').value);
                if (isNaN(timelineDays) || timelineDays < 1) {
                    showError('timelineDays', 'Please enter a valid timeline (at least 1 day)');
                    isValid = false;
                } else {
                    clearError('timelineDays');
                }

                const technicalStatement = document.getElementById('technicalStatement').value.trim();
                if (technicalStatement.length < 50) {
                    showError('technicalStatement', 'Technical compliance statement must be at least 50 characters');
                    isValid = false;
                } else if (technicalStatement.length > 600) {
                    showError('technicalStatement', 'Technical compliance statement cannot exceed 600 characters');
                    isValid = false;
                } else {
                    clearError('technicalStatement');
                }

                if (!document.getElementById('declaration').checked) {
                    alert('You must confirm the declaration before submitting.');
                    isValid = false;
                }

                if (!document.getElementById('termsAgreement').checked) {
                    alert('You must agree to the terms and conditions before submitting.');
                    isValid = false;
                }

                if (isValid) {
                    document.getElementById('submitBtn').disabled = true;
                    document.getElementById('submitBtn').innerHTML = 'Submitting Bid...';
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