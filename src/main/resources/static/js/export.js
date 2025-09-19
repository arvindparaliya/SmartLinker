console.log("export.js");
async function exportData() {
    try {
        Swal.fire({
            title: 'Preparing Export...',
            text: 'Please wait while we prepare your Excel file',
            allowOutsideClick: false,
            didOpen: () => {
                Swal.showLoading();
            }
        });

        // Get all contact IDs from the table
        const contactIds = [];
        const rows = document.querySelectorAll('#contact-table tbody tr');
        rows.forEach(row => {
            const viewButton = row.querySelector('button[onclick*="loadContactdata"]');
            if (viewButton) {
                const match = viewButton.getAttribute('onclick').match(/loadContactdata\(['"]([^'"]+)['"]\)/);
                if (match && match[1]) {
                    contactIds.push(match[1]);
                }
            }
        });

        // Fetch complete data for all contacts
        const contactsData = [];
        for (const id of contactIds) {
            const response = await fetch(`${baseURL}/api/contacts/${id}`, {
                credentials: "include"
            });
            if (response.ok) {
                const contact = await response.json();
                contactsData.push(contact);
            }
        }

        // Create export table
        const exportTable = document.createElement('table');

        // Create headers
        const headerRow = exportTable.insertRow();
        ["Created At", "Name", "Email", "Phone", "Address",
            "Description", "LinkedIn", "Website", "Favorite"].forEach(headerText => {
                const header = headerRow.insertCell();
                header.textContent = headerText;
            });

        // Add data rows
        contactsData.forEach(contact => {
            const row = exportTable.insertRow();

            const cells = [
                contact.createdAt ? new Date(contact.createdAt).toLocaleString() : '',
                contact.name || '',
                contact.email || '',
                contact.phoneNumber || '',
                contact.address || '',
                contact.description || '',
                contact.linkedInLink || '',
                contact.websiteLink || '',
                contact.favorite ? 'Yes' : 'No'
            ];

            cells.forEach(cellData => {
                const cell = row.insertCell();
                cell.textContent = cellData;
            });
        });

        // Convert to Excel
        TableToExcel.convert(exportTable, {
            name: "contacts.xlsx",
            sheet: {
                name: "Contacts"
            }
        });

        setTimeout(() => {
            Swal.close();
            Swal.fire({
                icon: 'success',
                title: 'Export Complete',
                text: 'Your contacts have been exported successfully',
                timer: 2000,
                showConfirmButton: false
            });
        }, 1000);

    } catch (error) {
        Swal.fire({
            icon: 'error',
            title: 'Export Failed',
            text: 'Error: ' + error.message
        });
    }
}
