<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ProcureGov - Ministry of Public Works Lesotho</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ministry-style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/landing.css">
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/images/favicon.ico">
</head>
<body>

    <!-- Top Government Banner -->
    <div class="gov-banner">
        <div class="banner-content">
            <span>GOVERNMENT OF LESOTHO</span>
        </div>
    </div>

    <!-- Header -->
    <header class="landing-header">
        <div class="header-container">
            <div class="logo-section">
                <div class="ministry-logo">
                    <span class="logo-icon">🏛️</span>
                    <div class="logo-text">
                        <h1>MINISTRY OF PUBLIC WORKS</h1>
                        <h2>KINGDOM OF LESOTHO</h2>
                    </div>
                </div>
            </div>
            <div class="header-actions">
                <a href="${pageContext.request.contextPath}/login.jsp" class="btn btn-outline-light">Sign In</a>
                <a href="${pageContext.request.contextPath}/supplier/register" class="btn btn-light">Register</a>
            </div>
        </div>
    </header>

    <!-- Hero Section -->
    <section class="hero-section">
        <div class="hero-overlay"></div>
        <div class="hero-content">
            <div class="hero-badge">Official Government Platform</div>
            <h1 class="hero-title">ProcureGov</h1>
            <p class="hero-subtitle">Government e-Tender Management System</p>
            <p class="hero-description">
                A secure, transparent, and efficient platform for managing government procurement tenders.
                Connect with the Ministry of Public Works and participate in public infrastructure projects.
            </p>
            <div class="hero-buttons">
                <a href="${pageContext.request.contextPath}/login.jsp" class="btn btn-primary btn-lg">
                    <span>Secure Login</span>
                    <span>→</span>
                </a>
                <a href="${pageContext.request.contextPath}/supplier/register" class="btn btn-outline-white btn-lg">
                    <span>Register as Supplier</span>
                    <span>→</span>
                </a>
            </div>
            <div class="hero-stats">
                <div class="stat-item">
                    <span class="stat-value">500+</span>
                    <span class="stat-label">Tenders Published</span>
                </div>
                <div class="stat-item">
                    <span class="stat-value">200+</span>
                    <span class="stat-label">Registered Suppliers</span>
                </div>
                <div class="stat-item">
                    <span class="stat-value">M 50M+</span>
                    <span class="stat-label">Contracts Awarded</span>
                </div>
            </div>
        </div>
    </section>

    <!-- Features Section -->
    <section class="features-section">
        <div class="container">
            <div class="section-header">
                <h2>Why Choose ProcureGov?</h2>
                <p>A modern, secure platform for government procurement</p>
            </div>
            
            <div class="features-grid">
                <div class="feature-card">
                    <div class="feature-icon">📋</div>
                    <h3>Browse Open Tenders</h3>
                    <p>Access all published government tenders in one place. Download tender notices and specifications.</p>
                </div>
                
                <div class="feature-card">
                    <div class="feature-icon">🔒</div>
                    <h3>Sealed Bidding</h3>
                    <p>Submit secure, sealed electronic bids. Your bid remains confidential until the closing date.</p>
                </div>
                
                <div class="feature-card">
                    <div class="feature-icon">📊</div>
                    <h3>Real-Time Tracking</h3>
                    <p>Track your bid status from submission to award. Receive notifications at every stage.</p>
                </div>
                
                <div class="feature-card">
                    <div class="feature-icon">⚖️</div>
                    <h3>Fair Evaluation</h3>
                    <p>Multi-criteria weighted scoring ensures transparent and objective bid evaluation.</p>
                </div>
                
                <div class="feature-card">
                    <div class="feature-icon">📧</div>
                    <h3>Instant Notifications</h3>
                    <p>Receive email updates on tender publications, bid statuses, and award announcements.</p>
                </div>
                
                <div class="feature-card">
                    <div class="feature-icon">🛡️</div>
                    <h3>Secure & Compliant</h3>
                    <p>Built to Lesotho government security standards with role-based access control.</p>
                </div>
            </div>
        </div>
    </section>

    <!-- How It Works Section -->
    <section class="how-it-works">
        <div class="container">
            <div class="section-header">
                <h2>How It Works</h2>
                <p>Simple steps to participate in government tenders</p>
            </div>
            
            <div class="steps-container">
                <div class="step-item">
                    <div class="step-number">1</div>
                    <div class="step-content">
                        <h3>Register Your Company</h3>
                        <p>Create a supplier account with your company details and documentation.</p>
                    </div>
                </div>
                
                <div class="step-arrow">→</div>
                
                <div class="step-item">
                    <div class="step-number">2</div>
                    <div class="step-content">
                        <h3>Browse & Select Tenders</h3>
                        <p>Explore open tenders and download detailed specifications.</p>
                    </div>
                </div>
                
                <div class="step-arrow">→</div>
                
                <div class="step-item">
                    <div class="step-number">3</div>
                    <div class="step-content">
                        <h3>Submit Your Bid</h3>
                        <p>Prepare and submit your sealed bid before the closing date.</p>
                    </div>
                </div>
                
                <div class="step-arrow">→</div>
                
                <div class="step-item">
                    <div class="step-number">4</div>
                    <div class="step-content">
                        <h3>Track & Get Awarded</h3>
                        <p>Monitor your bid status and receive award notifications.</p>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- User Roles Section -->
    <section class="roles-section">
        <div class="container">
            <div class="section-header">
                <h2>Who Can Use ProcureGov?</h2>
                <p>Designed for all stakeholders in the procurement process</p>
            </div>
            
            <div class="roles-grid">
                <div class="role-card">
                    <div class="role-icon">🏢</div>
                    <h3>Suppliers</h3>
                    <p>Registered companies and individuals bidding on government tenders.</p>
                    <ul class="role-features">
                        <li>Browse open tenders</li>
                        <li>Submit sealed bids</li>
                        <li>Track bid status</li>
                        <li>Receive award notices</li>
                    </ul>
                    <a href="${pageContext.request.contextPath}/supplier/register" class="btn btn-outline-primary">
                        Register as Supplier
                    </a>
                </div>
                
                <div class="role-card featured">
                    <div class="role-icon">📋</div>
                    <h3>Procurement Officers</h3>
                    <p>Ministry officials managing the tender lifecycle.</p>
                    <ul class="role-features">
                        <li>Create and publish tenders</li>
                        <li>Manage submissions</li>
                        <li>Oversee evaluation</li>
                        <li>Award contracts</li>
                    </ul>
                    <a href="${pageContext.request.contextPath}/login.jsp" class="btn btn-primary">
                        Staff Login
                    </a>
                </div>
                
                <div class="role-card">
                    <div class="role-icon">⭐</div>
                    <h3>Evaluation Committee</h3>
                    <p>Appointed officials scoring and evaluating bids.</p>
                    <ul class="role-features">
                        <li>Review submissions</li>
                        <li>Score bids</li>
                        <li>View consolidated results</li>
                        <li>Ensure fair evaluation</li>
                    </ul>
                    <a href="${pageContext.request.contextPath}/login.jsp" class="btn btn-outline-primary">
                        Committee Login
                    </a>
                </div>
            </div>
        </div>
    </section>

    <!-- CTA Section -->
    <section class="cta-section">
        <div class="container">
            <div class="cta-content">
                <h2>Ready to Participate?</h2>
                <p>Join hundreds of suppliers already using ProcureGov to access government contracts.</p>
                <div class="cta-buttons">
                    <a href="${pageContext.request.contextPath}/supplier/register" class="btn btn-light btn-lg">
                        Register Now
                    </a>
                    <a href="${pageContext.request.contextPath}/login.jsp" class="btn btn-outline-light btn-lg">
                        Sign In
                    </a>
                </div>
            </div>
        </div>
    </section>

    <!-- Contact Section -->
    <section class="contact-section">
        <div class="container">
            <div class="contact-grid">
                <div class="contact-info">
                    <h3>Need Assistance?</h3>
                    <p>Our ICT Support team is available to help you with any questions.</p>
                    <div class="contact-details">
                        <div class="contact-item">
                            <span class="contact-icon">📞</span>
                            <span>+266 5740 8184</span>
                        </div>
                        <div class="contact-item">
                            <span class="contact-icon">📧</span>
                            <span>kolisang.phatela@bothouniversity.com</span>
                        </div>
                        <div class="contact-item">
                            <span class="contact-icon">🕐</span>
                            <span>Monday - Friday, 08:00 - 16:30</span>
                        </div>
                        <div class="contact-item">
                            <span class="contact-icon">📍</span>
                            <span>Ministry of Public Works, Maseru, Lesotho</span>
                        </div>
                    </div>
                </div>
                <div class="quick-links">
                    <h3>Quick Links</h3>
                    <ul>
                        <li><a href="${pageContext.request.contextPath}/login.jsp">Login to Portal</a></li>
                        <li><a href="${pageContext.request.contextPath}/supplier/register">Supplier Registration</a></li>
                        <li><a href="#">About ProcureGov</a></li>
                        <li><a href="#">Terms and Conditions</a></li>
                        <li><a href="#">Privacy Policy</a></li>
                        <li><a href="#">FAQ</a></li>
                    </ul>
                </div>
            </div>
        </div>
    </section>

    <!-- Footer -->
    <footer class="landing-footer">
        <div class="container">
            <div class="footer-content">
                <div class="footer-logo">
                    <span>🏛️</span>
                    <span>ProcureGov</span>
                </div>
                <p class="footer-text">
                    &copy; 2026 Ministry of Public Works, Kingdom of Lesotho. All rights reserved.
                </p>
                <p class="footer-small">
                    This system is for authorized use only. All activities are logged and monitored.
                </p>
            </div>
        </div>
    </footer>

    <script>
        // Smooth scroll for anchor links
        document.querySelectorAll('a[href^="#"]').forEach(anchor => {
            anchor.addEventListener('click', function (e) {
                e.preventDefault();
                const target = document.querySelector(this.getAttribute('href'));
                if (target) {
                    target.scrollIntoView({ behavior: 'smooth' });
                }
            });
        });
        
        // Add scroll animation for features
        const observerOptions = {
            threshold: 0.1,
            rootMargin: '0px 0px -50px 0px'
        };
        
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.style.opacity = '1';
                    entry.target.style.transform = 'translateY(0)';
                }
            });
        }, observerOptions);
        
        document.querySelectorAll('.feature-card, .role-card, .step-item').forEach(el => {
            el.style.opacity = '0';
            el.style.transform = 'translateY(20px)';
            el.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
            observer.observe(el);
        });
    </script>

</body>
</html>