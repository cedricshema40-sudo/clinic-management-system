import React from 'react';

const LibraryHeader = ({header, payment, availableCourse, prerequisites }) => {
    return (

        <div>
            <header>
                <h1>{header}</h1>
                <h1>{payment} ----  {availableCourse} --- {prerequisites}</h1>
            </header>
        </div>
        
    );
}

export default LibraryHeader;