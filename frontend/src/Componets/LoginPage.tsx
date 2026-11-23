
import React, { useState } from 'react';
import LibrayButton from './LibrayButton';

const LoginPage = () => {

    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');


    const [studentID, setStudentID] = useState('');
    const [email, setEmail] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');


    const [doYouHaveAccount, setDoYouHaveAccount] = useState(true);

    function handleLogin() {
        alert("Your Username: " + username + "Your Password: " + password);
    }

    return (
        <div>

            {doYouHaveAccount ? (


                <div style={{ border: "3px solid blue", marginTop: "200px", padding: "20px" }}>

                    <h1>Sign In</h1>
                    <h3>Access your academic portal</h3>

                    <label>Username:</label>
                    <input type="text" onChange={e => setUsername(e.target.value)} placeholder="Enter your username" /> <br />

                    <label>Password:</label>
                    <input type="password" onChange={e => setPassword(e.target.value)} /><br />

                    <div>
                        <LibrayButton ButtonLabel="LOGIN" actionButton={handleLogin} /><br />
                        <label>New here?</label>
                        <LibrayButton ButtonLabel="Create an Account"actionButton={() => setDoYouHaveAccount(false)}/>
                    </div>

                </div>

            ) : (

                <div style={{ border: "3px solid indigo", marginTop: "200px", padding: "20px" }}>

                    <h1>Create an Account</h1>
                    <h3>Access your academic portal</h3>

                    <label>Student ID:</label>
                    <input type="text" onChange={e => setStudentID(e.target.value)} placeholder="Enter your student ID" />
                    <br />

                    <label>Email:</label>
                    <input type="email" onChange={e => setEmail(e.target.value)} />
                    <br />

                    <label>Password:</label>
                    <input type="password" onChange={e => setPassword(e.target.value)} />
                    <br />

                    <label>Confirm Password:</label>
                    <input type="password" onChange={e => setConfirmPassword(e.target.value)} />
                    <br />

                    <div>
                        <LibrayButton ButtonLabel="CREATE AN ACCOUNT" />
                        <br />

                        <label>Already have an account?</label>

                        <LibrayButton ButtonLabel="Sign In" actionButton={() => setDoYouHaveAccount(true)}
                        />
                    </div>

                </div>

            )}

        </div>
    );
};

export default LoginPage;
