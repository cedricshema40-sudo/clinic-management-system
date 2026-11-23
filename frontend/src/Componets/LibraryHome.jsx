import React from 'react';

function LibraryHome() {
    const [isCreateAccountButtonClicked, setIsCreateAccountButtonClicked] = React.useState(false);

    const books = [
        {
            code:"FSD001",
            name:"Full Stack Development",
            price: 100
        },
         {
            code:"SOD001",
            name:"Software Engineering",
            price: 900
        },
        {
            code:"FIN001",
            name:"Finance management",
            price: 2400
        },
        {
            code:"ACC001",
            name:"Accounting",
            price: 3000
        }
    ];

    return (
        <div>
         
            <ul>
                {books.map((book) => (
                    <li key={book.code}>
                        <h3>{book.name}</h3>
                        <p>${book.price}</p>
                    </li>
                ))}
            </ul>
            <div>{isCreateAccountButtonClicked ? <div>Create Account</div>: <div>Login</div>}</div>
       
       <div>The Toatl Price for //the number tital alll books that are i our</div>
       <div>the total price is: ${books.reduce((total, book) => total + book.price,0)}</div>
        </div>



    );
}

export default LibraryHome;