<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Privacy Policy - ProcureGov</title>
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
                    <h1>Privacy Policy</h1>
                    <p class="subtitle">Last Updated: April 2026</p>
                </div>

                <div class="content-section">
                    <h2>1. Introduction</h2>
                    <p>The Ministry of Public Works ("we," "our," or "us") is committed to protecting the privacy of users ("you," "your") of the ProcureGov e-Tender Management System. This Privacy Policy explains how we collect, use, disclose, and safeguard your information.</p>
                </div>

                <div class="content-section">
                    <h2>2. Information We Collect</h2>
                    <h3>2.1 Personal Information</h3>
                    <p>We collect personal information that you voluntarily provide during registration, including:</p>
                    <ul>
                        <li>Company/Individual name</li>
                        <li>Email address</li>
                        <li>Physical address</li>
                        <li>Contact number</li>
                        <li>Tax clearance and business registration details</li>
                    </ul>
                    <h3>2.2 Automatically Collected Information</h3>
                    <p>We automatically collect certain information when you use the System, including:</p>
                    <ul>
                        <li>IP address</li>
                        <li>Browser type and version</li>
                        <li>Access times and dates</li>
                        <li>Pages viewed and actions taken</li>
                    </ul>
                </div>

                <div class="content-section">
                    <h2>3. How We Use Your Information</h2>
                    <p>We use the information we collect to:</p>
                    <ul>
                        <li>Create and manage user accounts</li>
                        <li>Process tender submissions and bids</li>
                        <li>Communicate regarding tender updates and awards</li>
                        <li>Improve the System's functionality and security</li>
                        <li>Comply with legal and regulatory requirements</li>
                    </ul>
                </div>

                <div class="content-section">
                    <h2>4. Information Sharing and Disclosure</h2>
                    <p>We do not sell or rent your personal information to third parties. We may share information:</p>
                    <ul>
                        <li>With authorized Ministry personnel for procurement purposes</li>
                        <li>As required by law or legal process</li>
                        <li>To protect the rights, property, or safety of the Ministry, users, or the public</li>
                    </ul>
                </div>

                <div class="content-section">
                    <h2>5. Data Security</h2>
                    <p>We implement appropriate technical and organizational measures to protect your personal information from unauthorized access, disclosure, alteration, or destruction. However, no method of transmission over the internet is 100% secure.</p>
                </div>

                <div class="content-section">
                    <h2>6. Data Retention</h2>
                    <p>We retain personal information for as long as necessary to fulfill the purposes outlined in this Privacy Policy, unless a longer retention period is required by law.</p>
                </div>

                <div class="content-section">
                    <h2>7. Your Rights</h2>
                    <p>You have the right to:</p>
                    <ul>
                        <li>Access and obtain a copy of your personal information</li>
                        <li>Request correction of inaccurate information</li>
                        <li>Request deletion of your personal information</li>
                        <li>Object to or restrict processing of your information</li>
                    </ul>
                </div>

                <div class="content-section">
                    <h2>8. Cookies</h2>
                    <p>The System uses session cookies to maintain user authentication and preferences. These cookies are essential for the System's functionality and expire when you close your browser.</p>
                </div>

                <div class="content-section">
                    <h2>9. Third-Party Links</h2>
                    <p>The System may contain links to third-party websites. We are not responsible for the privacy practices or content of such websites.</p>
                </div>

                <div class="content-section">
                    <h2>10. Changes to This Privacy Policy</h2>
                    <p>We may update this Privacy Policy from time to time. We will notify users of significant changes by posting a notice on the System.</p>
                </div>

                <div class="content-section">
                    <h2>11. Contact Us</h2>
                    <p>If you have questions about this Privacy Policy, please contact:</p>
                    <div class="contact-details">
                        <p>Data Protection Officer</p>
                        <p>ICT Directorate, Ministry of Public Works</p>
                        <p>📞 +266 5740 8184</p>
                        <p>📧 kolisang.phatela@bothouniversity.com</p>
                    </div>
                </div>
            </div>
        </main>

        <jsp:include page="/shared/footer.jsp" />

    </body>
</html>