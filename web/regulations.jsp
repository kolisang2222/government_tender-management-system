<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Procurement Regulations - ProcureGov</title>
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
                    <h1>Procurement Regulations</h1>
                    <p class="subtitle">Legal Framework Governing Public Procurement in Lesotho</p>
                </div>

                <div class="content-section">
                    <h2>Overview</h2>
                    <p>Public procurement in the Kingdom of Lesotho is governed by a comprehensive legal framework designed to ensure transparency, fairness, competition, and value for money in the use of public funds.</p>
                </div>

                <div class="content-section">
                    <h2>Key Legislation</h2>
                    <h3>Public Procurement Act, 2023</h3>
                    <p>The Public Procurement Act establishes the legal framework for public procurement in Lesotho, including:</p>
                    <ul>
                        <li>Establishment of the Public Procurement Authority</li>
                        <li>Procurement methods and procedures</li>
                        <li>Tender evaluation and award criteria</li>
                        <li>Complaint and review mechanisms</li>
                        <li>Debarment and sanctions</li>
                    </ul>

                    <h3>Public Procurement Regulations, 2024</h3>
                    <p>The Regulations provide detailed implementation guidance for the Act, including:</p>
                    <ul>
                        <li>Threshold values for different procurement methods</li>
                        <li>Tender documentation requirements</li>
                        <li>Evaluation committee composition and procedures</li>
                        <li>Contract management requirements</li>
                    </ul>
                </div>

                <div class="content-section">
                    <h2>Procurement Principles</h2>
                    <p>All procurement conducted through ProcureGov adheres to the following principles:</p>
                    <ul>
                        <li><strong>Transparency:</strong> Tender opportunities are publicly advertised and evaluation criteria are disclosed.</li>
                        <li><strong>Fairness:</strong> Equal treatment of all suppliers with consistent evaluation procedures.</li>
                        <li><strong>Competition:</strong> Open competition to obtain the best value for public funds.</li>
                        <li><strong>Value for Money:</strong> Balancing cost, quality, and sustainability considerations.</li>
                        <li><strong>Accountability:</strong> Clear audit trails and documentation of procurement decisions.</li>
                        <li><strong>Integrity:</strong> Prevention of corruption and conflicts of interest.</li>
                    </ul>
                </div>

                <div class="content-section">
                    <h2>Tender Thresholds</h2>
                    <table class="threshold-table">
                        <thead>
                            <tr>
                                <th>Procurement Method</th>
                                <th>Threshold Value (Maloti)</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>Direct Procurement</td>
                                <td>Up to M 50,000</td>
                            </tr>
                            <tr>
                                <td>Request for Quotations</td>
                                <td>M 50,001 - M 500,000</td>
                            </tr>
                            <tr>
                                <td>National Competitive Bidding</td>
                                <td>M 500,001 - M 5,000,000</td>
                            </tr>
                            <tr>
                                <td>International Competitive Bidding</td>
                                <td>Above M 5,000,000</td>
                            </tr>
                        </tbody>
                    </table>
                </div>

                <div class="content-section">
                    <h2>Supplier Eligibility</h2>
                    <p>To participate in government tenders, suppliers must:</p>
                    <ul>
                        <li>Be legally registered to conduct business in Lesotho</li>
                        <li>Possess a valid Tax Clearance Certificate from the Lesotho Revenue Authority</li>
                        <li>Not be debarred or suspended from public procurement</li>
                        <li>Meet any additional qualification criteria specified in the tender document</li>
                    </ul>
                </div>

                <div class="content-section">
                    <h2>Bid Evaluation Criteria</h2>
                    <p>The ProcureGov System uses the following weighted evaluation criteria:</p>
                    <ul>
                        <li><strong>Price Score (40%):</strong> Based on the bid amount relative to the lowest compliant bid.</li>
                        <li><strong>Technical Compliance Score (35%):</strong> Assessment of the bid's technical merit.</li>
                        <li><strong>Delivery Timeline Score (25%):</strong> Based on proposed completion time.</li>
                    </ul>
                </div>

                <div class="content-section">
                    <h2>Complaints and Appeals</h2>
                    <p>Suppliers may file a complaint regarding the procurement process. Complaints must be submitted in writing within 7 calendar days of the event giving rise to the complaint. The Ministry will acknowledge receipt and provide a written response within 14 calendar days.</p>
                </div>

                <div class="content-section">
                    <h2>Ethical Conduct</h2>
                    <p>All users of the ProcureGov System are expected to adhere to the highest standards of ethical conduct. Prohibited activities include:</p>
                    <ul>
                        <li>Collusion between suppliers</li>
                        <li>Bribery or attempted bribery of procurement officials</li>
                        <li>Submission of false or misleading information</li>
                        <li>Conflicts of interest</li>
                    </ul>
                    <p>Violations may result in debarment from future procurement opportunities and referral to law enforcement authorities.</p>
                </div>

                <div class="content-section">
                    <h2>Further Information</h2>
                    <p>For complete procurement regulations and guidance, please contact:</p>
                    <div class="contact-details">
                        <p><strong>Procurement Policy Unit</strong></p>
                        <p>Ministry of Public Works</p>
                        <p>P.O. Box 20, Maseru 100, Lesotho</p>
                        <p>📞 +266 2232 1000</p>
                        <p>📧 procurement@mpw.gov.ls</p>
                    </div>
                </div>
            </div>
        </main>

        <jsp:include page="/shared/footer.jsp" />

    </body>
</html>