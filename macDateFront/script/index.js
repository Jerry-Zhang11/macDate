const signinToggle = document.getElementById('signinToggle');
const signupToggle = document.getElementById('signupToggle');
const toggleSlider = document.getElementById('toggleSlider');
const signinForm = document.getElementById('signinForm');
const signupForm = document.getElementById('signupForm');

function switchToSignin() {
    signinToggle.classList.add('active');
    signupToggle.classList.remove('active');
    toggleSlider.classList.remove('signup');
    signinForm.classList.remove('hidden');
    signupForm.classList.add('hidden');
    document.querySelector('.logo p').textContent = 'Welcome back';
}

function switchToSignup() {
    signupToggle.classList.add('active');
    signinToggle.classList.remove('active');
    toggleSlider.classList.add('signup');
    signupForm.classList.remove('hidden');
    signinForm.classList.add('hidden');
    document.querySelector('.logo p').textContent = 'Create your account';
}

signinToggle.addEventListener('click', switchToSignin);
signupToggle.addEventListener('click', switchToSignup);

function togglePassword(inputId, button) {
    const input = document.getElementById(inputId);
    if (input.type === 'password') {
        input.type = 'text';
        button.textContent = '🙈';
    } else {
        input.type = 'password';
        button.textContent = '👁️';
    }
}


// Add form submission handlers
document.querySelector('#signinForm form').addEventListener('submit', async function(e) {
    e.preventDefault();
    const email = document.getElementById('signin-email').value;
    const password = document.getElementById('signin-password').value;


    // Signin logic here
    try{
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                email,
                password
            })
        })

        const data = await response.json();

        if (response.ok){
            const authData = {
                token: data.token,
                expiresAt: new Date(Date.now() + (parseInt(data.expiresIn))).toISOString(),
                expiresIn: data.expiresIn
            };

            localStorage.setItem('auth', JSON.stringify(authData));

            if (data.isNewUser){
                window.location.href = `signup/gender.html`
            }
            else{
                window.location.href = `home.html`
            }
        }
        else if(response.status === 401){
            alert(data.messsage || 'Invalid Email or Password');
        }
        else if (response.status === 403){
            alert("Account not verified");
        }
        else {
            // Handle error response
            alert(data.message || 'Signin failed. Please try again.');}

    }
    catch (error) {
        console.error('Signin error:', error);
        alert('Network error. Please check your connection and try again.');
    }
});




document.querySelector('#signupForm form').addEventListener('submit', async function(e) {
    e.preventDefault();
    const username = document.getElementById('signup-name').value;
    const email = document.getElementById('signup-email').value;
    const password = document.getElementById('signup-password').value;
    const confirmPassword = document.getElementById('signup-confirm').value;

    if (password !== confirmPassword) {
        alert('Passwords do not match!');
        return;
    }

    // Signup logic here
    try {
        const response = await fetch('/api/auth/signup', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                email,
                password,
                username
            })
        });

        const data = await response.json();

        if (response.ok) {
            // Successful signup - redirect to verification page
            window.location.href = `verify-email.html?email=${encodeURIComponent(email)}`;
        } else {
            // Handle error response
            alert(data.message || 'Signup failed. Please try again.');
        }
    } catch (error) {
        console.error('Signup error:', error);
        alert('Network error. Please check your connection and try again.');
    }
});


// Add smooth transitions for input focus
document.querySelectorAll('input').forEach(input => {
    input.addEventListener('focus', function() {
        this.parentElement.style.transform = 'translateY(-2px)';
    });

    input.addEventListener('blur', function() {
        this.parentElement.style.transform = 'translateY(0)';
    });
});