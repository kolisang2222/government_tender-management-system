<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>About ProcureGov - Ministry of Public Works Lesotho</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ministry-style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/static-page.css">
        <style>
            /* ============================================================
   PROCUREGOV ABOUT PAGE STYLES
   Ministry of Public Works, Kingdom of Lesotho
   Modern, clean, and consistent with the main system
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

            /* ========== HEADER (matches landing but simplified) ========== */
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
                border-radius: 4px;
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

            /* Content Sections */
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

            .content-section p {
                color: var(--slate);
                line-height: 1.7;
                margin-bottom: 0.75rem;
            }

            /* Features Grid (inside content) */
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
                color: var(--slate);
                margin-bottom: 0;
            }

            /* Benefits List */
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

            /* Contact Details */
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
            }
        </style>
    </head>
    <body>

        <!-- Top Government Banner -->
        <div class="gov-banner">
            <div class="banner-content">
                <span>GOVERNMENT OF LESOTHO</span>
            </div>
        </div>

        <!-- Header -->
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

        <!-- Main Content -->
        <main class="static-main">
            <div class="container">
                <div class="page-header">
                    <h1>About ProcureGov</h1>
                    <p class="subtitle">Government e-Tender Management System</p>
                </div>

                <div class="content-section">
                    <h2>Overview</h2>
                    <p>ProcureGov is the official electronic tender management system of the Ministry of Public Works, Kingdom of Lesotho. This platform digitizes the complete tender lifecycle, from publication to award, ensuring transparency, efficiency, and fairness in government procurement.</p>
                </div>

                <div class="content-section">
                    <h2>Our Mission</h2>
                    <p>To modernize government procurement through a secure, transparent, and accessible digital platform that connects the Ministry of Public Works with qualified suppliers across Lesotho and beyond.</p>
                </div>

                <div class="content-section">
                    <h2>Key Features</h2>
                    <div class="features-grid">
                        <div class="feature-item">
                            <h3>📋 Digital Tender Publication</h3>
                            <p>All government tenders are published online, accessible to registered suppliers 24/7.</p>
                        </div>
                        <div class="feature-item">
                            <h3>🔒 Sealed Bidding</h3>
                            <p>Bids remain sealed and confidential until the official closing date and time.</p>
                        </div>
                        <div class="feature-item">
                            <h3>⚖️ Fair Evaluation</h3>
                            <p>Multi-criteria weighted scoring ensures objective and transparent bid evaluation.</p>
                        </div>
                        <div class="feature-item">
                            <h3>📊 Real-Time Tracking</h3>
                            <p>Track the status of tenders and bids throughout the entire procurement lifecycle.</p>
                        </div>
                        <div class="feature-item">
                            <h3>📧 Instant Notifications</h3>
                            <p>Receive email updates on tender publications, bid statuses, and award announcements.</p>
                        </div>
                        <div class="feature-item">
                            <h3>🛡️ Secure & Compliant</h3>
                            <p>Built to Lesotho government security standards with role-based access control.</p>
                        </div>
                    </div>
                </div>

                <div class="content-section">
                    <h2>Benefits</h2>
                    <ul class="benefits-list">
                        <li><strong>For Suppliers:</strong> Equal access to tender opportunities, reduced paperwork, and real-time bid tracking.</li>
                        <li><strong>For Procurement Officers:</strong> Streamlined tender management, automated evaluation tools, and comprehensive reporting.</li>
                        <li><strong>For Government:</strong> Increased transparency, reduced procurement costs, and better audit trails.</li>
                        <li><strong>For Citizens:</strong> Greater visibility into how public funds are spent on infrastructure projects.</li>
                    </ul>
                </div>

                <div class="content-section">
                    <h2>Contact Information</h2>
                    <div class="contact-details">
                        <p><strong>ICT Directorate</strong></p>
                        <p>Ministry of Public Works</p>
                        <p>P.O. Box 20, Maseru 100, Lesotho</p>
                        <p>📞 +266 5740 8184</p>
                        <p>📧 kolisang.phatela@bothouniversity.com</p>
                        <p>🕐 Monday - Friday, 08:00 - 16:30</p>
                    </div>
                </div>
            </div>
        </main>

        <jsp:include page="/shared/footer.jsp" />

    </body>
</html>