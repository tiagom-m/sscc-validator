const API_BASE = "/api/v1/sscc";

const validateInputEl = document.getElementById("sscc-input");
const resultEl = document.getElementById("result");
const listEl = document.getElementById("sscc-list");
const countEl = document.getElementById("count");
const emptyEl = document.getElementById("empty-state");

const clearBtn = document.getElementById("clear-btn");
const validateBtn = document.getElementById("validate-btn");

// Event listeners
validateBtn.addEventListener("click", validate);
clearBtn.addEventListener("click", clearList);
validateInputEl.addEventListener("keydown", (e) => {
  if (e.key === "Enter") validate();
});

// Load existing list on page start
loadList();

async function validate() {
  const sscc = validateInputEl.value.trim();
  if (!sscc) {
    showResult("error", "Please enter an SSCC code");
    return;
  }

  validateBtn.disabled = true;
  validateBtn.textContent = "Validating...";

  try {
    const response = await fetch(API_BASE, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ sscc }),
    });

    if (!response.ok) {
      const error = await response.json();
      showResult("error", error.errorMessage);
      return;
    }

    showResult("success", "Valid SSCC — added it to the list");
    validateInputEl.value = "";
    validateInputEl.focus();

    await loadList();
  } catch (err) {
    showResult("error", "Connection error: " + err.message);
  } finally {
    validateBtn.disabled = false;
    validateBtn.textContent = "Validate";
  }
}

async function loadList() {
  try {
    const response = await fetch(API_BASE, {
      method: "GET",
    });

    if (!response.ok) throw new Error("Failed to load list");

    const ssccs = await response.json();
    renderList(ssccs);
  } catch (err) {
    showResult("error", err.message);
  }
}

function renderList(ssccs) {
  listEl.innerHTML = "";
  countEl.textContent = ssccs.length;

  if (ssccs.length === 0) {
    emptyEl.style.display = "block";
    clearBtn.style.display = "none";
    return;
  }

  emptyEl.style.display = "none";
  clearBtn.style.display = "inline-block";

  // Show newest first
  for (let i = ssccs.length - 1; i >= 0; i--) {
    const li = document.createElement("li");
    li.innerHTML = "<span>" + ssccs[i] + "</span>";
    listEl.appendChild(li);
  }
}

async function clearList() {
  try {
    const response = await fetch(API_BASE, {
      method: "DELETE",
    });

    if (!response.ok) throw new Error("Failed to clear list");

    renderList([]);
    showResult("info", "List cleared");
  } catch (err) {
    showResult("error", err.message);
  }
}

function showResult(type, message) {
  resultEl.className = "result " + type;
  resultEl.textContent = message;
}
