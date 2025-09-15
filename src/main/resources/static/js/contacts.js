console.log("Contacts.js initialized");

const baseURL = "http://localhost:8081";
const DEFAULT_IMAGE = '/images/dprofile.png';

//  Modal Init Helper -
function initContactModal() {
  const modalEl = document.getElementById("view_contact_modal");
  if (!modalEl) return false;
  if (window.contactModal) return true; 

  window.contactModal = new Modal(modalEl, {
    placement: "center",
    backdrop: "dynamic",
    backdropClasses: "bg-gray-900/50 dark:bg-gray-900/80 fixed inset-0 z-40",
    closable: true,
    onHide: () => {
      console.log("Contact modal hidden");
      resetModalContent();
    },
    onShow: () => console.log("Contact modal shown")
  }, {
    id: "view_contact_modal",
    override: true
  });

  console.log("contactModal initialized");
  return true;
}

document.addEventListener("DOMContentLoaded", () => {
  console.log("Contacts.js initialized");

  if (!initContactModal()) {
    console.warn("Modal element #view_contact_modal not found at DOMContentLoaded — observing DOM...");
    const observer = new MutationObserver((mutations, obs) => {
      if (initContactModal()) {
        obs.disconnect();
      }
    });
    observer.observe(document.body, { childList: true, subtree: true });
  }
});

// Reset modal content when closed 
function resetModalContent() {
  document.getElementById("contact_image").src = DEFAULT_IMAGE;
  document.getElementById("contact_name").textContent = 'Loading...';
  document.getElementById("contact_email").textContent = 'Loading...';
  document.getElementById("contact_phone").textContent = 'Loading...';
  document.getElementById("contact_address").textContent = 'Loading...';
  document.getElementById("contact_about").textContent = 'Loading...';
  document.getElementById("contact_favorite").textContent = 'Loading...';
  document.getElementById("contact_website").href = '#';
  document.getElementById("contact_website").textContent = 'Loading...';
  document.getElementById("contact_linkedIn").href = '#';
  document.getElementById("contact_linkedIn").textContent = 'Loading...';
  document.getElementById("other_link").href = '#';
  document.getElementById("other_link").textContent = 'Loading...';
}

//  Enhanced image loader
function loadContactImage(imgElement, imageUrl) {
  imgElement.onerror = function () {
    this.src = DEFAULT_IMAGE;
    this.onerror = null;
  };
 if (imageUrl) { imgElement.src = imageUrl; } else { imgElement.src = DEFAULT_IMAGE; }
}

// Load contact data
async function loadContactdata(id) {
  try {
    console.log("Loading contact data for id:", id);

    if (!window.contactModal && !initContactModal()) {
      console.error("Modal element not found at load time");
      Swal.fire({ icon: 'error', title: 'Modal missing', text: 'Contact modal not present in DOM.' });
      return;
    }

    const res = await fetch(`${baseURL}/api/contacts/${id}`, { credentials: "include" });
    if (!res.ok) throw new Error("Failed to fetch contact");

    const contact = await res.json();
    console.log("Contact loaded:", contact);

    document.getElementById("contact_image").src = contact.picture ;
    document.getElementById("contact_name").textContent = contact.name || "N/A";
    document.getElementById("contact_email").textContent = contact.email || "N/A";
    document.getElementById("contact_phone").textContent = contact.phoneNumber || "N/A";
    document.getElementById("contact_address").textContent = contact.address || "N/A";
    document.getElementById("contact_about").textContent = contact.description || "N/A";
    document.getElementById("contact_favorite").textContent = contact.favorite ? "Yes" : "No";

    const websiteEl = document.getElementById("contact_website");
    websiteEl.href = contact.website || "#";
    websiteEl.textContent = contact.website || "N/A";

    const linkedInEl = document.getElementById("contact_linkedIn");
    linkedInEl.href = contact.linkedInLink || "#";
    linkedInEl.textContent = contact.linkedInLink || "N/A";

    const otherEl = document.getElementById("other_link");
    otherEl.href = contact.otherLink || "#";
    otherEl.textContent = contact.otherLink || "N/A";

    // Profile Image Handling
// let imageUrl = DEFAULT_IMAGE;

// // f backend already gives a full image URL
// if (contact.imageUrl) {
//   imageUrl = contact.imageUrl;
// }

// // If backend gives only Cloudinary publicId
// else if (contact.cloudinaryImagePublicId) {
//   // Replace <your-cloud-name> with your Cloudinary cloud name
//   imageUrl = `https://res.cloudinary.com/dh9tbs4hc/image/upload/${contact.cloudinaryImagePublicId}.jpg`;
// }

// loadContactImage(document.getElementById("contact_image"), imageUrl);


    // Finally show modal
    window.contactModal.show();
  } catch (error) {
    console.error("Error loading contact:", error);
    Swal.fire({ icon: 'error', title: 'Failed to load contact', text: error.message });
    if (window.contactModal) window.contactModal.hide();
  }
}

async function deleteContact(id) {
  const result = await Swal.fire({
    title: "Delete Contact?",
    text: "You won't be able to revert this!",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "Yes, delete it!",
    cancelButtonText: "Cancel",
    customClass: {
      confirmButton: "bg-red-600 hover:bg-red-700 text-white font-semibold py-2 px-4 rounded transition-all duration-150",
      cancelButton: "bg-gray-200 hover:bg-gray-300 text-gray-800 font-semibold py-2 px-4 rounded ml-2 transition-all duration-150 dark:bg-gray-700 dark:text-gray-200 dark:hover:bg-gray-600"
    },
    buttonsStyling: false
  });

  if (result.isConfirmed) {
    try {
      const response = await fetch(`${baseURL}/user/contacts/delete/${id}`, {
        method: 'GET',
        credentials: 'include'
      });

      if (response.ok) {
        // Success toast
        Swal.fire({
          icon: 'success',
          title: 'Deleted!',
          text: 'The contact has been deleted.',
          timer: 1000,
          showConfirmButton: false,
          toast: true,
          timerProgressBar: true,
          customClass: {
            popup: "bg-white dark:bg-gray-800 text-gray-900 dark:text-white shadow-md rounded-lg"
          }
        });

        
        setTimeout(() => window.location.reload(), 2100);

      } else {
        throw new Error('Delete failed');
      }
    } catch (error) {
      Swal.fire({ icon: 'error', title: 'Delete Failed', text: error.message });
    }
  }
}


// Debugg links 
document.addEventListener("click", function (e) {
  if (e.target.tagName === "A") {
    console.log("Link clicked:", e.target.href);
  }
});



// Shae it lobally
window.loadContactdata = loadContactdata;
window.deleteContact = deleteContact;
window.openContactModal = () => contactModal.show();
window.closeContactModal = () => contactModal.hide();
