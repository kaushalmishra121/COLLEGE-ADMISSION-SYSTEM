let applications = [
    { id: 101, name: "Aarav Sharma", email: "aarav@example.com", phone: "+91 9876543210", score: 92.4, program: "Computer Science & Engineering", status: "Accepted" },
    { id: 102, name: "Neha Patel", email: "neha.p@example.com", phone: "+91 9811223344", score: 88.5, program: "Artificial Intelligence & Machine Learning", status: "Pending" },
    { id: 103, name: "Rohan Verma", email: "rohan.v@example.com", phone: "+91 9822334455", score: 81.2, program: "Information Technology", status: "Pending" },
    { id: 104, name: "Priya Nair", email: "priya.n@example.com", phone: "+91 9833445566", score: 86.7, program: "Electronics & Telecommunication Engineering", status: "Accepted" }
];

// Open modal helper
function openModal(id) {
    const modal = document.getElementById(id);
    if (modal) {
        modal.style.display = "flex";
        if (id === 'adminModal') {
            loadApplications();
        }
    }
}

// Close modal helper
function closeModal(id) {
    const modal = document.getElementById(id);
    if (modal) {
        modal.style.display = "none";
    }
}

// Close modal on background click
window.onclick = function (e) {
    if (e.target.classList.contains('modal')) {
        e.target.style.display = "none";
    }
};

// Handle Admission Form Submission
document.getElementById('admissionForm').addEventListener('submit', async function (e) {
    e.preventDefault();

    const name = document.getElementById('fullName').value.trim();
    const email = document.getElementById('email').value.trim();
    const phone = document.getElementById('phone').value.trim();
    const score = parseFloat(document.getElementById('score').value);
    const course = document.getElementById('course').value;

    const newApp = {
        id: 100 + applications.length + 1,
        name: name,
        email: email,
        phone: phone,
        score: score,
        program: course,
        status: "Pending"
    };

    // Attempt to persist to backend API
    try {
        const response = await fetch('/api/applications', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                name: name,
                email: email,
                phone: phone,
                score: score,
                program: course
            })
        });

        if (response.ok) {
            const data = await response.json();
            if (data.id) {
                newApp.id = data.id;
            }
        }
    } catch (err) {
        console.warn("Backend server offline, saved in local session memory:", err);
    }

    // Add to local state
    applications.unshift(newApp);

    // Reset form
    this.reset();
    closeModal('applyModal');

    // Populate Printable Receipt Modal
    const now = new Date();
    const formattedDate = now.toLocaleDateString('en-US', {
        month: 'short',
        day: 'numeric',
        year: 'numeric'
    }) + ', ' + now.toLocaleTimeString('en-US', {
        hour: 'numeric',
        minute: '2-digit',
        hour12: true
    });

    document.getElementById('receiptAppId').innerText = '#' + newApp.id;
    document.getElementById('receiptDate').innerText = formattedDate;
    document.getElementById('receiptName').innerText = newApp.name;
    document.getElementById('receiptEmail').innerText = newApp.email;
    document.getElementById('receiptPhone').innerText = newApp.phone || phone;
    document.getElementById('receiptScore').innerText = newApp.score.toFixed(2) + '%';
    document.getElementById('receiptProgram').innerText = newApp.program;

    // Automatically display the printable acknowledgement receipt
    openModal('receiptModal');
});

// Load applications from API or fallback array
async function loadApplications() {
    try {
        const response = await fetch('/api/applications');
        if (response.ok) {
            const data = await response.json();
            if (Array.isArray(data) && data.length > 0) {
                applications = data;
            }
        }
    } catch (err) {
        console.warn("Backend unavailable, using cached records:", err);
    }
    renderTable();
}

// Render Admin Verification Dashboard Table
function renderTable() {
    const tbody = document.getElementById('applicationTableBody');
    if (!tbody) return;

    if (applications.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" style="text-align:center; padding:20px; color:#64748b;">No applications submitted yet.</td></tr>';
        return;
    }

    tbody.innerHTML = applications.map(app => {
        let badgeClass = app.status === 'Accepted' ? 'badge-accepted' : app.status === 'Rejected' ? 'badge-rejected' : 'badge-pending';
        return `
      <tr>
        <td>#${app.id}</td>
        <td><strong>${escapeHtml(app.name)}</strong></td>
        <td>${escapeHtml(app.email)}</td>
        <td>${escapeHtml(app.phone || '-')}</td>
        <td>${app.score}%</td>
        <td>${escapeHtml(app.program)}</td>
        <td><span class="badge ${badgeClass}">${app.status}</span></td>
        <td>
          <button class="status-btn" onclick="updateStatus(${app.id}, 'Accepted')">Accept</button>
          <button class="status-btn" onclick="updateStatus(${app.id}, 'Rejected')">Reject</button>
        </td>
      </tr>
    `;
    }).join('');
}

// Update admission status
function updateStatus(id, newStatus) {
    const target = applications.find(a => a.id === id);
    if (target) {
        target.status = newStatus;
        renderTable();
    }
}

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;");
}