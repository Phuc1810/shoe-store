const variantButtons = document.querySelectorAll(".product-variant-option");
const mainImage = document.querySelector("#mainProductImage");
const displayPrice = document.querySelector("#displayPrice");
const selectedColor = document.querySelector("#selectedColor");
const selectedSize = document.querySelector("#selectedSize");
const selectedStock = document.querySelector("#selectedStock");
const selectedSku = document.querySelector("#selectedSku");
const selectedVariantId = document.querySelector("#selectedVariantId");
const quantityInput = document.querySelector("#quantity");
const addToCartButton = document.querySelector("#addToCartButton");

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

    if (selectedVariantId) {
        selectedVariantId.value = button.dataset.variantId || "";
    }

    if (quantityInput) {
        const stock = Number.parseInt(button.dataset.stock || "0", 10);
        quantityInput.max = Number.isNaN(stock) ? "" : String(stock);
        if (stock > 0 && Number.parseInt(quantityInput.value || "1", 10) > stock) {
            quantityInput.value = String(stock);
        }
    }

    if (addToCartButton) {
        const stock = Number.parseInt(button.dataset.stock || "0", 10);
        addToCartButton.disabled = !button.dataset.variantId || Number.isNaN(stock) || stock <= 0;
    }
}

variantButtons.forEach((button) => {
    button.addEventListener("click", () => applyVariant(button));
});

applyVariant(document.querySelector(".product-variant-option.active") || variantButtons[0]);
