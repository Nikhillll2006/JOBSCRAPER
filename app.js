// JavaScript code to handle Login, Sign-Up, and Forgot Password functionality

// Function to handle sign-up
function handleSignUp(event) {
    event.preventDefault();

    const username = document.getElementById('username').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirm-password').value;

    if (password !== confirmPassword) {
        alert('Passwords do not match!');
        return;
    }

    const users = JSON.parse(localStorage.getItem('users')) || [];

    // Check if the username or email already exists
    const userExists = users.find(user => user.username === username || user.email === email);

    if (userExists) {
        alert('Username or Email already exists!');
        return;
    }

    const newUser = {
        username: username,
        email: email,
        password: password
    };

    users.push(newUser);
    localStorage.setItem('users', JSON.stringify(users));

    alert('Sign-Up successful!');
    window.location.href = 'login.html';
}

// Function to handle login
function handleLogin(event) {
    event.preventDefault();

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    // Retrieve users from localStorage
    const users = JSON.parse(localStorage.getItem('users')) || [];

    // Check if the username and password match any user
    const user = users.find(user => user.username === username && user.password === password);

    if (!user) {
        alert('Invalid username or password!');
        return;
    }

    alert('Login successful!');
    window.location.href = 'dashboard.html'; // Redirect to dashboard after login
}

// Function to handle forgot password
function handleForgotPassword(event) {
    event.preventDefault();

    const email = document.getElementById('email').value;

    const users = JSON.parse(localStorage.getItem('users')) || [];

    const user = users.find(user => user.email === email);

    if (!user) {
        alert('No account found with that email address!');
        return;
    }

    alert('Password reset link has been sent to your email! (Simulated)');
    window.location.href = 'login.html';
}

// Function to handle profile form submission
function handleProfileForm(event) {
    event.preventDefault();

    const profileData = {
        fullname: document.getElementById('fullname').value,
        age: document.getElementById('age').value,
        gender: document.getElementById('gender').value,
        email: document.getElementById('email').value,
        phone: document.getElementById('phone').value,
        address: document.getElementById('address').value,
        jobTitle: document.getElementById('jobTitle').value,
        experience: document.getElementById('experience').value,
        skills: document.getElementById('skills').value,
        resume: document.getElementById('resume').files[0]?.name || '', // Store file name if a file is selected
        qualification: document.getElementById('qualification').value,
        university: document.getElementById('university').value,
        passingYear: document.getElementById('passingYear').value,
        certifications: document.getElementById('certifications').value
    };

    localStorage.setItem('profile', JSON.stringify(profileData));

    alert('Profile saved successfully!');
}

// Function to attach event listeners to forms
function attachEventListeners() {
    const signUpForm = document.getElementById('signup-form');
    const loginForm = document.getElementById('login-form');
    const forgotPasswordForm = document.getElementById('forgot-password-form');

    if (signUpForm) {
        signUpForm.addEventListener('submit', handleSignUp);
    }

    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }

    if (forgotPasswordForm) {
        forgotPasswordForm.addEventListener('submit', handleForgotPassword);
    }
}

// Attach event listener for profile form
function attachProfileFormListener() {
    const profileForm = document.getElementById('profile-form');
    if (profileForm) {
        profileForm.addEventListener('submit', handleProfileForm);
    }
}

// Call these functions after DOM is fully loaded
document.addEventListener('DOMContentLoaded', () => {
    attachEventListeners(); // Attach login/signup/forgot password listeners
    attachProfileFormListener(); // Attach profile form listener
});