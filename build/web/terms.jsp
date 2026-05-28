<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Terms of Use - ProcureGov</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ministry-style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/static-page.css">
        <style>
            /* ============================================================
       PROCUREGOV STATIC PAGES (About / Contact / Terms)
       Ministry of Public Works, Kingdom of Lesotho
       Modern, clean, consistent with the main system
       ============================================================ */

            :root {
                --navy: #0B2B4F;
                --navy-dark: #071E38;
                --navy-light: #1E3A6F;
                --forest: #2C5E3A;
                --forest-light: #3B7A4C;
                --slate: #4A5B6E;
                --slate-light: #7A8B9F;
                --slate-lighter: #C8D2DC;
                --gray-bg: #F8F9FC;
                --white: #FFFFFF;
                --shadow-sm: 0 2px 6px rgba(0, 0, 0, 0.05), 0 1px 2px rgba(0, 0, 0, 0.03);
                --shadow-md: 0 8px 20px rgba(0, 0, 0, 0.06);
                --radius-sm: 4px;
                --radius-md: 6px;
                --transition: 0.2s ease;
            }

            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }

            body {
                font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                background: var(--gray-bg);
                color: var(--slate);
                line-height: 1.5;
                font-size: 16px;
            }

            /* ========== GOVERNMENT BANNER ========== */
            .gov-banner {
                background: var(--navy);
                padding: 8px 0;
                text-align: center;
            }

            .gov-banner .banner-content span {
                color: white;
                font-size: 12px;
                font-weight: 600;
                letter-spacing: 2px;
            }

            /* ========== HEADER ========== */
            .static-header {
                background: var(--white);
                box-shadow: var(--shadow-sm);
                position: sticky;
                top: 0;
                z-index: 100;
            }

            .header-container {
                max-width: 1200px;
                margin: 0 auto;
                padding: 16px 24px;
                display: flex;
                align-items: center;
                justify-content: space-between;
                flex-wrap: wrap;
                gap: 20px;
            }

            .logo-link {
                display: flex;
                align-items: center;
                gap: 16px;
                text-decoration: none;
            }

            .logo-icon {
                font-size: 40px;
            }

            .logo-text h1 {
                font-size: 16px;
                font-weight: 700;
                color: var(--navy);
                letter-spacing: 1px;
                margin-bottom: 2px;
            }

            .logo-text h2 {
                font-size: 12px;
                font-weight: 500;
                color: var(--slate-light);
                letter-spacing: 2px;
            }

            .header-actions {
                display: flex;
                gap: 12px;
            }

            /* ========== BUTTONS ========== */
            .btn {
                display: inline-flex;
                align-items: center;
                gap: 8px;
                padding: 10px 20px;
                font-size: 14px;
                font-weight: 600;
                text-decoration: none;
                border-radius: var(--radius-sm);
                transition: var(--transition);
                border: 2px solid transparent;
                cursor: pointer;
            }

            .btn-primary {
                background: var(--forest);
                color: white;
            }

            .btn-primary:hover {
                background: var(--forest-light);
                transform: translateY(-1px);
                box-shadow: 0 4px 10px rgba(44, 94, 58, 0.25);
            }

            .btn-outline-primary {
                background: transparent;
                border-color: var(--navy);
                color: var(--navy);
            }

            .btn-outline-primary:hover {
                background: rgba(11, 43, 79, 0.05);
                transform: translateY(-1px);
            }

            /* ========== MAIN CONTENT ========== */
            .static-main {
                padding: 2rem 0 3rem;
            }

            .container {
                max-width: 1000px;
                margin: 0 auto;
                padding: 0 24px;
            }

            /* Page Header */
            .page-header {
                text-align: center;
                margin-bottom: 2.5rem;
            }

            .page-header h1 {
                font-size: 2.5rem;
                font-weight: 700;
                color: var(--navy-dark);
                margin-bottom: 0.5rem;
            }

            .page-header .subtitle {
                font-size: 1.125rem;
                color: var(--slate-light);
            }

            /* Content Sections (cards) */
            .content-section {
                background: var(--white);
                border-radius: var(--radius-md);
                box-shadow: var(--shadow-sm);
                padding: 1.75rem;
                margin-bottom: 2rem;
                transition: box-shadow var(--transition);
            }

            .content-section:hover {
                box-shadow: var(--shadow-md);
            }

            .content-section h2 {
                font-size: 1.5rem;
                font-weight: 600;
                color: var(--navy);
                margin-bottom: 1rem;
                padding-bottom: 0.5rem;
                border-bottom: 2px solid var(--gray-bg);
            }

            .content-section h3 {
                font-size: 1.125rem;
                font-weight: 600;
                color: var(--navy);
                margin-top: 1.25rem;
                margin-bottom: 0.5rem;
            }

            .content-section h3:first-of-type {
                margin-top: 0;
            }

            .content-section p {
                color: var(--slate);
                line-height: 1.7;
                margin-bottom: 0.75rem;
            }

            /* Lists (for Terms of Use, etc.) */
            .content-section ul, .content-section ol {
                margin: 0.75rem 0 0.75rem 1.75rem;
                color: var(--slate);
                line-height: 1.7;
            }

            .content-section li {
                margin-bottom: 0.5rem;
            }

            .content-section li:last-child {
                margin-bottom: 0;
            }

            /* Contact Details (used on Contact & Terms pages) */
            .contact-details {
                background: var(--gray-bg);
                padding: 1.25rem;
                border-radius: var(--radius-md);
                margin-top: 0.5rem;
            }

            .contact-details p {
                margin-bottom: 0.5rem;
                color: var(--slate);
            }

            .contact-details p strong {
                color: var(--navy);
                font-weight: 600;
            }

            /* Features Grid (About page) */
            .features-grid {
                display: grid;
                grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
                gap: 1.5rem;
                margin-top: 0.5rem;
            }

            .feature-item {
                background: var(--gray-bg);
                padding: 1.25rem;
                border-radius: var(--radius-md);
                transition: transform var(--transition);
            }

            .feature-item:hover {
                transform: translateY(-2px);
            }

            .feature-item h3 {
                font-size: 1rem;
                font-weight: 600;
                color: var(--navy);
                margin-bottom: 0.5rem;
            }

            .feature-item p {
                font-size: 0.875rem;
                margin-bottom: 0;
            }

            /* Benefits List (About page) */
            .benefits-list {
                list-style: none;
                padding-left: 0;
            }

            .benefits-list li {
                padding: 0.75rem 0;
                border-bottom: 1px solid var(--slate-lighter);
                color: var(--slate);
            }

            .benefits-list li:last-child {
                border-bottom: none;
            }

            .benefits-list li strong {
                color: var(--navy);
                font-weight: 600;
            }

            .benefits-list li::before {
                content: "✓";
                color: var(--forest);
                font-weight: bold;
                margin-right: 12px;
            }

            /* ========== RESPONSIVE ========== */
            @media (max-width: 768px) {
                .header-container {
                    flex-direction: column;
                    text-align: center;
                }
                .logo-link {
                    flex-direction: column;
                }
                .logo-text h1, .logo-text h2 {
                    text-align: center;
                }
                .static-main {
                    padding: 1.25rem 0;
                }
                .page-header h1 {
                    font-size: 2rem;
                }
                .features-grid {
                    grid-template-columns: 1fr;
                }
                .content-section {
                    padding: 1.25rem;
                }
            }

            @media (max-width: 480px) {
                .container {
                    padding: 0 16px;
                }
                .btn {
                    padding: 8px 16px;
                    font-size: 13px;
                }
                .page-header h1 {
                    font-size: 1.75rem;
                }
                .content-section ul, .content-section ol {
                    margin-left: 1.25rem;
                }
            }
        </style>
    </head>
    <body>

        <div class="gov-banner">
            <div class="banner-content">
                <span>GOVERNMENT OF LESOTHO</span>
            </div>
        </div>

        <header class="static-header">
            <div class="header-container">
                <div class="logo-section">
                    <a href="${pageContext.request.contextPath}/" class="logo-link">
                        <span class="logo-icon">🏛️</span>
                        <div class="logo-text">
                            <h1>MINISTRY OF PUBLIC WORKS</h1>
                            <h2>KINGDOM OF LESOTHO</h2>
                        </div>
                    </a>
                </div>
                <div class="header-actions">
                    <a href="${pageContext.request.contextPath}/login.jsp" class="btn btn-outline-primary">Sign In</a>
                    <a href="${pageContext.request.contextPath}/supplier/register" class="btn btn-primary">Register</a>
                </div>
            </div>
        </header>

        <main class="static-main">
            <div class="container">
                <div class="page-header">
                    <h1>Terms of Use</h1>
                    <p class="subtitle">Last Updated: April 2026</p>
                </div>

                <div class="content-section">
                    <h2>1. Acceptance of Terms</h2>
                    <p>By accessing or using the ProcureGov e-Tender Management System ("the System"), you agree to be bound by these Terms of Use. If you do not agree to these terms, please do not use the System.</p>
                </div>

                <div class="content-section">
                    <h2>2. Definitions</h2>
                    <p><strong>"System"</strong> refers to the ProcureGov e-Tender Management System.</p>
                    <p><strong>"User"</strong> refers to any individual or entity accessing or using the System.</p>
                    <p><strong>"Supplier"</strong> refers to a registered company or individual submitting bids through the System.</p>
                    <p><strong>"Ministry"</strong> refers to the Ministry of Public Works, Kingdom of Lesotho.</p>
                </div>

                <div class="content-section">
                    <h2>3. User Accounts</h2>
                    <p>Users are responsible for maintaining the confidentiality of their account credentials. Any activity occurring under a user's account is the sole responsibility of the account holder. Users must immediately notify the Ministry of any unauthorized use of their account.</p>
                </div>

                <div class="content-section">
                    <h2>4. Supplier Registration</h2>
                    <p>Suppliers must provide accurate, complete, and current information during registration. The Ministry reserves the right to verify supplier information and reject or suspend accounts that provide false or misleading information.</p>
                </div>

                <div class="content-section">
                    <h2>5. Bid Submission</h2>
                    <p>All bids submitted through the System are legally binding offers. Suppliers may only submit one bid per tender. Bids cannot be modified after submission, except as explicitly permitted by the System during the open bidding period.</p>
                </div>

                <div class="content-section">
                    <h2>6. Confidentiality</h2>
                    <p>Bid information remains sealed and confidential until the official tender closing date and time. Suppliers agree not to attempt to access or view other suppliers' bid information.</p>
                </div>

                <div class="content-section">
                    <h2>7. Prohibited Conduct</h2>
                    <p>Users agree not to:</p>
                    <ul>
                        <li>Attempt to gain unauthorized access to the System</li>
                        <li>Submit false or fraudulent bids</li>
                        <li>Interfere with the proper functioning of the System</li>
                        <li>Attempt to manipulate the evaluation or award process</li>
                        <li>Use the System for any unlawful purpose</li>
                    </ul>
                </div>

                <div class="content-section">
                    <h2>8. Intellectual Property</h2>
                    <p>All content, software, and materials available through the System are the property of the Ministry of Public Works or its licensors and are protected by applicable intellectual property laws.</p>
                </div>

                <div class="content-section">
                    <h2>9. Disclaimer of Warranties</h2>
                    <p>The System is provided "as is" without warranties of any kind, either express or implied. The Ministry does not warrant that the System will be uninterrupted, error-free, or completely secure.</p>
                </div>

                <div class="content-section">
                    <h2>10. Limitation of Liability</h2>
                    <p>To the maximum extent permitted by law, the Ministry shall not be liable for any indirect, incidental, special, consequential, or punitive damages arising out of or related to the use of the System.</p>
                </div>

                <div class="content-section">
                    <h2>11. Modifications to Terms</h2>
                    <p>The Ministry reserves the right to modify these Terms of Use at any time. Continued use of the System after such modifications constitutes acceptance of the updated terms.</p>
                </div>

                <div class="content-section">
                    <h2>12. Governing Law</h2>
                    <p>These Terms of Use shall be governed by and construed in accordance with the laws of the Kingdom of Lesotho.</p>
                </div>

                <div class="content-section">
                    <h2>13. Contact Information</h2>
                    <p>For questions regarding these Terms of Use, please contact:</p>
                    <div class="contact-details">
                        <p>ICT Directorate</p>
                        <p>Ministry of Public Works</p>
                        <p>📞 +266 5740 8184</p>
                        <p>📧 kolisang.phatela@bothouniversity.com</p>
                    </div>
                </div>
            </div>
        </main>

        <jsp:include page="/shared/footer.jsp" />

    </body>
</html>