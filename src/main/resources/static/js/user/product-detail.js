const variantButtons = document.querySelectorAll(".product-variant-option");
const mainImage = document.querySelector("#mainProductImage");
const displayPrice = document.querySelector("#displayPrice");
const selectedColor = document.querySelector("#selectedColor");
const selectedSize = document.querySelector("#selectedSize");
const selectedStock = document.querySelector("#selectedStock");
const selectedSku = document.querySelector("#selectedSku");

function applyVariant(button) {
    if (!button) {
        return;
    }

    variantButtons.forEach((item) => item.classList.remove("active"));
    button.classList.add("active");

    if (mainImage && button.dataset.imageUrl) {
        mainImage.src = button.dataset.imageUrl;
    }

    if (displayPrice) {
        displayPrice.textContent = button.dataset.price || "Liên hệ";
    }

    if (selectedColor) {
        selectedColor.textContent = button.dataset.color || "Chưa chọn";
    }

    if (selectedSize) {
        selectedSize.textContent = button.dataset.size || "Chưa chọn";
    }

    if (selectedStock) {
        selectedStock.textContent = button.dataset.stock || "0";
    }

    if (selectedSku) {
        selectedSku.textContent = button.dataset.sku || "Chưa có";
    }
}

variantButtons.forEach((button) => {
    button.addEventListener("click", () => applyVariant(button));
});

applyVariant(document.querySelector(".product-variant-option.active") || variantButtons[0]);
