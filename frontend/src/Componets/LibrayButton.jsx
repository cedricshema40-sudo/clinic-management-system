import React from 'react';

function LibrayButton({ ButtonLabel, actionButton }) {
    return (
        <div>
            <button onClick={actionButton}>
                {ButtonLabel}
            </button>
        </div>
    );
}

export default LibrayButton;