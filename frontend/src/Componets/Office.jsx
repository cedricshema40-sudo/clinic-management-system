
import { useState } from 'react';
import './Office.css';

function Office() {

    const [officeName, setOfficeName] = useState("");
    const [officeNumber, setOfficeNumber] = useState("");

    async function createOffice(e) {
        e.preventDefault();

        try {
            const response = await fetch(
                "http://localhost:8082/api/offices/save",
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({
                        name: officeName,
                        officeNumber: officeNumber
                    })
                }
            );

            if (!response.ok) {
                const errorMessage = await response.text();
                throw new Error(errorMessage || "Failed to save office");
            }

            const savedOffice = await response.json();

            alert("Office saved successfully!");

            console.log("Saved Office:", savedOffice);

            setOfficeName("");
            setOfficeNumber("");

        } catch (error) {
            alert("Office not saved. " + error.message);
            console.log("Error:", error);
        }
    }

    return (
        <div className="office-container">

            <h1>Save Office</h1>

            <form onSubmit={createOffice}>

                <label>Office Name:</label>

                <input
                    type="text"
                    placeholder="Enter office name"
                    value={officeName}
                    onChange={(e) => setOfficeName(e.target.value)}
                    required
                />

                <br /><br />

                <label>Office Number:</label>

                <input
                    type="text"
                    placeholder="Enter office number"
                    value={officeNumber}
                    onChange={(e) => setOfficeNumber(e.target.value)}
                    required
                />

                <br /><br />

                <button type="submit">
                    Save Office
                </button>

            </form>

        </div>
    );
}

export default Office;
