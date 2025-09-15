console.log("profile.js initialized");

const baseURL = "http://localhost:8081";
const DEFAULT_IMAGE = '/images/dprofile.png';

//  MODAL SETUP 
const viewProfileModal = document.getElementById("view_profile_modal");

if (viewProfileModal) {
  const options = {
    placement: "bottom-right",
    backdrop: "dynamic",
    backdropClasses: "bg-gray-900/50 dark:bg-gray-900/80 fixed inset-0 z-40",
    closable: true,
    onHide: () => console.log("Profile modal hidden"),
    onShow: () => setTimeout(() => profileModal.classList.add("scale-100"), 50),
    onToggle: () => console.log("Profile modal toggled"),
  };

  const instanceOptions = {
    id: "view_profile_modal", 
    override: true,
  };

  const profileModal = new Modal(viewProfileModal, options, instanceOptions);

  window.openProfileModal = () => profileModal.show();
  window.closeProfileModal = () => profileModal.hide();
} else {
  console.warn("Profile modal element not found");
}

// LOAD USER DATA 
async function loadUserdata(id) {
  if (!id) return console.error("User ID is required");
  try {
    const res = await fetch(`${baseURL}/api/user/${id}`);
    const data = await res.json();
    console.log("User data:", data);

    // Set data with fallbacks
    document.querySelector("#user_name").innerText = data.name || "No Name";
    document.querySelector("#user_email").innerText = data.email || "No Email";
    document.querySelector("#user_phone").innerText = data.phoneNumber || "No Phone";
    document.querySelector("#user_about").innerText = data.description || "No information provided";

    const userImage = document.querySelector("#user_image");
    if (userImage) userImage.src = data.profilePic || DEFAULT_IMAGE;

    // Open modal after loading data
    viewProfileModal();
  } catch (error) {
    console.error("Failed to load user data:", error);
  }
}

// Enhanced image loader
function loadUserImage(imgElement, imageUrl) {
  imgElement.onerror = function() {
    this.src = DEFAULT_IMAGE;
    this.onerror = null; 
  };
  
  if (imageUrl) {
    imgElement.src = imageUrl;
  } else {
    imgElement.src = DEFAULT_IMAGE;
  }
}

document.addEventListener("DOMContentLoaded", () => {
  console.log("Profile page DOM loaded");

  const profileUpdateForm = document.getElementById("profileUpdateForm");

  if (profileUpdateForm) {
    profileUpdateForm.addEventListener("submit", async function(e) {
      e.preventDefault();

      const formData = new FormData(profileUpdateForm);

      try {
        const res = await fetch(profileUpdateForm.action, {
          method: "POST",
          body: formData
        });

        if (res.ok) {
          alert("Profile updated successfully!");
          window.location.href = "/user/profile";
        } else {
          const text = await res.text();
          console.error("Update failed:", text);
          alert("Profile update failed!");
        }
      } catch (error) {
        console.error("Error:", error);
        alert("Profile update failed!");
      }
    });
  } else {
    console.log("profileUpdateForm not found orr skipping form handler");
  }
});



// Preview profile image before upload
function previewProfileImage(event) {
    const input = event.target;
    const preview = document.getElementById("profileImagePreview");
    if (input.files && input.files[0]) {
        const reader = new FileReader();
        reader.onload = (e) => {
            if (preview) preview.src = e.target.result;
        };
        reader.readAsDataURL(input.files[0]);
    }
}

