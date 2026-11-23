
import { useState, useEffect } from 'react';
import './OfficeList.css';

function OfficeList() {

    const [offices, setOffices] = useState([]);
    const [search, setSearch] = useState("");


    async function getOffices() {

        try {
            const response = await fetch(
                "http://localhost:8082/api/offices/all"
            );

            if (!response.ok) {
                throw new Error("Failed to load offices");
            }

            const data = await response.json();

            setOffices(data);

        } catch (error) {
            console.log(error);
            alert("Failed to load offices");
        }
    }

    useEffect(() => {
        getOffices();
    }, []);

    async function deleteOffice(id) {

        const confirmDelete = window.confirm(
            "Are you sure you want to delete this office?"
        );

        if (!confirmDelete) {
            return;
        }

        try {
            const response = await fetch(
                `http://localhost:8082/api/offices/${id}`,
                {
                    method: "DELETE"
                }
            );

            if (!response.ok) {
                throw new Error("Failed to delete office");
            }

            alert("Office deleted successfully!");

            getOffices();

        } catch (error) {
            console.log(error);
            alert("Failed to delete office");
        }
    }

    const filteredOffices = offices.filter((office) =>
        office.name.toLowerCase().includes(search.toLowerCase()) ||
        office.officeNumber.toString().includes(search)
    );

    return (
        <div className="office-list-container">

            <h1>Available Offices</h1>

            <input
                type="text"
                placeholder="Search by office name or number"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                className="search-input"
            />

            <table>

                <thead>
                    <tr>
                        <th>Office Name</th>
                        <th>Office Number</th>
                        <th>Action</th>
                    </tr>
                </thead>

                <tbody>

                    {filteredOffices.length > 0 ? (

                        filteredOffices.map((office) => (

                            <tr key={office.id}>

                                <td>{office.name}</td>

                                <td>{office.officeNumber}</td>

                                <td>
                                    <button
                                        onClick={() => deleteOffice(office.id)}
                                        className="delete-button"
                                    >
                                        Delete
                                    </button>

                                    <button
                                        className="update-button"
                                        disabled
                                    >
                                        Update
                                    </button>
                                </td>

                            </tr>

                        ))

                    ) : (

                        <tr>
                            <td colSpan="3">No offices found.</td>
                        </tr>

                    )}

                </tbody>

            </table>

        </div>
    );
}

export default OfficeList;
