console.log("Contacts.js");
const baseURL = "http://localhost:8080";
// const baseURL = "https://www.scm20.site";
const viewContactModal = document.getElementById("view_contact_modal");

// options with default values
const options = {
  placement: "bottom-right",
  backdrop: "dynamic",
  backdropClasses: "bg-gray-900/50 dark:bg-gray-900/80 fixed inset-0 z-40",
  closable: true,
  onHide: () => {
    console.log("modal is hidden");
  },
  onShow: () => {
    setTimeout(() => {
      contactModal.classList.add("scale-100");
    }, 50);
  },
  onToggle: () => {
    console.log("modal has been toggled");
  },
};

// instance options object
const instanceOptions = {
  id: "view_contact_mdoal",
  override: true,
};

const contactModal = new Modal(viewContactModal, options, instanceOptions);

function openContactModal() {
  contactModal.show();
}

function closeContactModal() {
  contactModal.hide();
}

async function loadContactdata(id) {
  //function call to load data
  console.log(id);
  try {
      const response = await fetch(baseURL + `/api/contact/${id}`)
      if(!response){
        throw new Error(`HTTP !error status code: ${response.status}`);
      }
      const data = await response.json();
      console.log(data);
      document.getElementById("contact_name").innerHTML = data.name;
      document.getElementById("contact_email").innerHTML = data.email;
      document.getElementById("contact_phoneNumber").innerHTML = data.phoneNumber;
      document.getElementById("contact_picture").src = data.picture;
      console.log(data.picture);
      
      document.getElementById("contact_description").innerHTML = data.description;
      document.getElementById("contact_address").innerHTML = data.address;
      // document.getElementById("contact_favorite").innerHTML = data.favorite;
      const contactFavorite = document.querySelector("#contact_favorite");
      if (data.favorite == true) {
        contactFavorite.innerHTML = '<i class="fa-solid fa-heart" style="color: #b92222;"></i>';
      } else {
        contactFavorite.innerHTML = '<i class="fa-regular fa-heart"></i>';
      }
      document.getElementById("contact_websiteLink").href = data.websiteLink;
      document.getElementById("contact_websiteLink").innerHTML = data.websiteLink;
      // document.getElementById("contact_socialLink").innerHTML = data.socialLink;
      openContactModal();
      return data;
    } catch (error) {
    console.log(`error : ${error}`);
  }
}

// delete contact

async function deleteContact(id) {
  Swal.fire({
    title: "Do you want to delete the contact?",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "Delete",
  }).then((result) => {
    /* Read more about isConfirmed, isDenied below */
    if (result.isConfirmed) {
      const url = `${baseURL}/user/contacts/delete/` + id;
      window.location.replace(url);
    }
  });
}


function copyToClipboard(elementId,copyBtn) {
  console.log(`copy fn called ${elementId} ${copyBtn.id}` );
  
  let text = document.getElementById(elementId).innerText;
  navigator.clipboard.writeText(text).then(function() {
    copyBtn.innerHTML= '<i class="fa-solid fa-check"></i>';
    setTimeout(function() {
      copyBtn.innerHTML = '<i class="fa-regular fa-copy"></i>';
    }, 2000);
  }, function(err) {
    console.error('Could not copy text: ', err);
  });
}
