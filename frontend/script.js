const API_BASE_URL = "http://localhost:8080";

let cakes = [];
let cart = [];

let selectedCakeId = null;
let selectedRating = 0;


/* =========================================================
   PAGE LOAD
========================================================= */

document.addEventListener("DOMContentLoaded", () => {

    console.log("======================================");
    console.log("🍰 CAKE DELIGHT FRONTEND STARTED");
    console.log("======================================");

    loadCakes();
    loadOrders();
    updateCartUI();


    const checkoutForm =
        document.getElementById("checkout-form");

    if (checkoutForm) {

        checkoutForm.addEventListener(
            "submit",
            placeOrder
        );

    }


    const categoryFilter =
        document.getElementById("category-filter");

    if (categoryFilter) {

        categoryFilter.addEventListener(
            "change",
            filterCakes
        );

    }

});



/* =========================================================
   LOAD CAKES
========================================================= */

async function loadCakes() {

    const container =
        document.getElementById(
            "cake-container"
        );


    if (!container) {

        console.error(
            "cake-container not found"
        );

        return;
    }


    container.innerHTML = `
        <div class="loading-card">

            <div class="loading-spinner"></div>

            <p>
                Loading delicious cakes...
            </p>

        </div>
    `;


    try {

        const response =
            await fetch(
                `${API_BASE_URL}/api/cakes`
            );


        if (!response.ok) {

            throw new Error(
                `HTTP ${response.status}`
            );

        }


        const data =
            await response.json();


        cakes =
            Array.isArray(data)
                ? data
                : [];


        console.log(
            "🍰 Cakes loaded:",
            cakes
        );


        populateCategories();

        displayCakes(cakes);

        await loadRatingsForCakes();


    } catch (gatewayError) {

        console.warn(
            "API Gateway catalog request failed:",
            gatewayError
        );


        /* =====================================================
           DIRECT CATALOG FALLBACK
        ===================================================== */

        try {

            const response =
                await fetch(
                    "http://localhost:8081/api/cakes"
                );


            if (!response.ok) {

                throw new Error(
                    `HTTP ${response.status}`
                );

            }


            const data =
                await response.json();


            cakes =
                Array.isArray(data)
                    ? data
                    : [];


            populateCategories();

            displayCakes(cakes);

            await loadRatingsForCakes();


        } catch (catalogError) {

            console.error(
                "❌ Catalog Service failed:",
                catalogError
            );


            container.innerHTML = `

                <div class="empty-orders">

                    <div class="empty-icon">
                        😔
                    </div>

                    <h3>
                        Unable to load cakes
                    </h3>

                    <p>
                        Make sure Catalog Service
                        is running on port 8081.
                    </p>

                    <button
                        class="add-button"
                        onclick="loadCakes()"
                    >
                        🔄 Retry
                    </button>

                </div>

            `;

        }

    }

}



/* =========================================================
   POPULATE CATEGORIES
========================================================= */

function populateCategories() {

    const select =
        document.getElementById(
            "category-filter"
        );


    if (!select) {
        return;
    }


    const currentValue =
        select.value || "ALL";


    const categories = [
        ...new Set(
            cakes
                .map(
                    cake =>
                        String(
                            cake.category || ""
                        ).trim()
                )
                .filter(Boolean)
        )
    ].sort();


    select.innerHTML = `

        <option value="ALL">
            All Categories
        </option>

    `;


    categories.forEach(
        category => {

            const option =
                document.createElement(
                    "option"
                );


            option.value =
                category;

            option.textContent =
                category;


            select.appendChild(
                option
            );

        }
    );


    if (
        currentValue === "ALL" ||
        categories.includes(
            currentValue
        )
    ) {

        select.value =
            currentValue;

    } else {

        select.value =
            "ALL";

    }

}



/* =========================================================
   DISPLAY CAKES
========================================================= */

function displayCakes(
    cakeList
) {

    const container =
        document.getElementById(
            "cake-container"
        );


    if (!container) {
        return;
    }


    if (
        !Array.isArray(cakeList) ||
        cakeList.length === 0
    ) {

        container.innerHTML = `

            <div class="empty-orders">

                <div class="empty-icon">
                    🎂
                </div>

                <h3>
                    No cakes found
                </h3>

                <p>
                    Try another search or category.
                </p>

            </div>

        `;

        return;
    }


    container.innerHTML =
        cakeList
            .map(
                cake => {

                    const id =
                        Number(
                            cake.id
                        );


                    const name =
                        escapeHtml(
                            cake.name ||
                            "Cake"
                        );


                    const description =
                        escapeHtml(
                            cake.description ||
                            "Delicious freshly baked cake."
                        );


                    const category =
                        escapeHtml(
                            cake.category ||
                            "Other"
                        );


                    const price =
                        Number(
                            cake.price || 0
                        ).toFixed(0);


                    const image =
                        cake.imageUrl ||
                        cake.image ||
                        "https://images.unsplash.com/photo-1578985545062-69928b1d9587?auto=format&fit=crop&w=800&q=85";


                    const available =
                        cake.available !== false;


                    const rating =
                        Number(
                            cake.averageRating || 0
                        );


                    const reviewCount =
                        Number(
                            cake.reviewCount || 0
                        );


                    const ratingHTML =
                        createRatingHTML(
                            rating
                        );


                    const safeCakeName =
                        escapeJs(
                            cake.name ||
                            "Cake"
                        );


                    return `

                        <article
                            class="cake-card"
                            data-cake-id="${id}"
                        >

                            <div class="cake-image-wrapper">

                                <img
                                    class="cake-image"
                                    src="${escapeHtml(image)}"
                                    alt="${name}"
                                    loading="lazy"
                                    onerror="
                                        this.onerror=null;
                                        this.src='https://images.unsplash.com/photo-1578985545062-69928b1d9587?auto=format&fit=crop&w=800&q=85';
                                    "
                                >

                                <span class="cake-badge">
                                    ${category}
                                </span>

                            </div>


                            <div class="cake-info">

                                <h3>
                                    ${name}
                                </h3>


                                <p class="cake-description">
                                    ${description}
                                </p>


                                <div class="cake-rating">

                                    <span class="stars">
                                        ${ratingHTML}
                                    </span>


                                    <span class="rating-number">

                                        ${
                                            rating > 0
                                                ? rating.toFixed(2)
                                                : "New"
                                        }

                                    </span>


                                    <span class="review-count">

                                        ${
                                            reviewCount > 0
                                                ? `(${reviewCount} reviews)`
                                                : "(No reviews)"
                                        }

                                    </span>

                                </div>


                                <span class="category">
                                    ${category}
                                </span>


                                <div class="cake-bottom">

                                    <span class="price">
                                        ₹${price}
                                    </span>


                                    ${
                                        available
                                            ? `
                                                <span class="available">
                                                    ✓ Available
                                                </span>
                                            `
                                            : `
                                                <span class="unavailable">
                                                    ✕ Unavailable
                                                </span>
                                            `
                                    }

                                </div>


                                <button
                                    type="button"
                                    class="rate-button"
                                    onclick="openRatingModal(${id}, '${safeCakeName}')"
                                >
                                    ⭐ Rate this Cake
                                </button>


                                <button
                                    type="button"
                                    class="add-button"
                                    onclick="addToCart(${id})"
                                    ${
                                        !available
                                            ? "disabled"
                                            : ""
                                    }
                                >

                                    ${
                                        available
                                            ? "🛒 Add to Cart"
                                            : "Unavailable"
                                    }

                                </button>


                            </div>

                        </article>

                    `;

                }
            )
            .join("");

}



/* =========================================================
   CREATE STAR RATING
========================================================= */

function createRatingHTML(
    rating
) {

    const value =
        Math.max(
            0,
            Math.min(
                5,
                Number(rating) || 0
            )
        );


    let html = "";


    for (
        let i = 1;
        i <= 5;
        i++
    ) {

        html +=
            i <= Math.round(value)
                ? "★"
                : "☆";

    }


    return html;
}



/* =========================================================
   LOAD RATINGS
========================================================= */

async function loadRatingsForCakes() {

    if (
        !Array.isArray(cakes) ||
        cakes.length === 0
    ) {
        return;
    }


    console.log(
        "⭐ Loading ratings..."
    );


    await Promise.all(

        cakes.map(
            async cake => {

                try {

                    const cakeId =
                        Number(
                            cake.id
                        );


                    /* =================================================
                       GET ALL RATINGS
                       Used for review count
                    ================================================= */

                    const ratingsResponse =
                        await fetch(
                            `${API_BASE_URL}/api/ratings/cake/${cakeId}`
                        );


                    if (!ratingsResponse.ok) {

                        throw new Error(
                            `Ratings HTTP ${ratingsResponse.status}`
                        );

                    }


                    const ratings =
                        await ratingsResponse.json();


                    const ratingList =
                        Array.isArray(ratings)
                            ? ratings
                            : [];


                    /* =================================================
                       GET BACKEND CALCULATED AVERAGE
                    ================================================= */

                    const averageResponse =
                        await fetch(
                            `${API_BASE_URL}/api/ratings/cake/${cakeId}/average`
                        );


                    if (!averageResponse.ok) {

                        throw new Error(
                            `Average HTTP ${averageResponse.status}`
                        );

                    }


                    const averageData =
                        await averageResponse.json();


                    const averageRating =
                        Number(
                            averageData.averageRating
                        );


                    /* =================================================
                       VALID RATINGS
                    ================================================= */

                    const validRatings =
                        ratingList
                            .map(
                                item =>
                                    Number(
                                        item.rating
                                    )
                            )
                            .filter(
                                value =>
                                    Number.isFinite(
                                        value
                                    ) &&
                                    value >= 1 &&
                                    value <= 5
                            );


                    cake.reviewCount =
                        validRatings.length;


                    cake.averageRating =
                        Number.isFinite(
                            averageRating
                        )
                            ? averageRating
                            : 0;


                    console.log(
                        `⭐ Cake ${cakeId}:`,
                        cake.averageRating,
                        `(${cake.reviewCount} reviews)`
                    );


                } catch (error) {

                    console.warn(
                        `⚠️ Could not load rating for cake ${cake.id}:`,
                        error
                    );


                    cake.averageRating =
                        0;


                    cake.reviewCount =
                        0;

                }

            }
        )

    );


    console.log(
        "⭐ Ratings loaded:",
        cakes
    );


    displayCakes(
        cakes
    );

}



/* =========================================================
   OPEN RATING MODAL
========================================================= */

function openRatingModal(
    cakeId,
    cakeName
) {

    selectedCakeId =
        Number(
            cakeId
        );


    selectedRating =
        0;


    const modal =
        document.getElementById(
            "rating-modal"
        );


    const cakeNameElement =
        document.getElementById(
            "rating-cake-name"
        );


    const commentElement =
        document.getElementById(
            "rating-comment"
        );


    const selectedText =
        document.getElementById(
            "rating-selected-text"
        );


    if (cakeNameElement) {

        cakeNameElement.textContent =
            cakeName;

    }


    if (commentElement) {

        commentElement.value =
            "";

    }


    if (selectedText) {

        selectedText.textContent =
            "Select a rating";

    }


    updateRatingStars();


    if (modal) {

        modal.classList.add(
            "show"
        );


        document.body.classList.add(
            "no-scroll"
        );

    }

}



/* =========================================================
   CLOSE RATING MODAL
========================================================= */

function closeRatingModal() {

    const modal =
        document.getElementById(
            "rating-modal"
        );


    if (modal) {

        modal.classList.remove(
            "show"
        );

    }


    document.body.classList.remove(
        "no-scroll"
    );


    selectedCakeId =
        null;


    selectedRating =
        0;

}



/* =========================================================
   SELECT RATING
========================================================= */

function selectRating(
    value
) {

    selectedRating =
        Number(
            value
        );


    updateRatingStars();


    const selectedText =
        document.getElementById(
            "rating-selected-text"
        );


    const labels = {

        1: "⭐ Poor",

        2: "⭐⭐ Fair",

        3: "⭐⭐⭐ Good",

        4: "⭐⭐⭐⭐ Very Good",

        5: "⭐⭐⭐⭐⭐ Excellent"

    };


    if (selectedText) {

        selectedText.textContent =
            labels[selectedRating] ||
            "Select a rating";

    }

}



/* =========================================================
   UPDATE RATING STARS
========================================================= */

function updateRatingStars() {

    const container =
        document.getElementById(
            "rating-stars-input"
        );


    if (!container) {
        return;
    }


    const buttons =
        container.querySelectorAll(
            "button"
        );


    buttons.forEach(
        (
            button,
            index
        ) => {

            const value =
                index + 1;


            if (
                value <=
                selectedRating
            ) {

                button.classList.add(
                    "selected"
                );

            } else {

                button.classList.remove(
                    "selected"
                );

            }

        }
    );

}



/* =========================================================
   SUBMIT RATING
========================================================= */

async function submitRating() {

    if (!selectedCakeId) {

        showToast(
            "❌ Please select a cake."
        );

        return;
    }


    if (
        selectedRating < 1 ||
        selectedRating > 5
    ) {

        showToast(
            "⭐ Please select a rating from 1 to 5."
        );

        return;
    }


    const commentElement =
        document.getElementById(
            "rating-comment"
        );


    const comment =
        commentElement
            ? commentElement.value.trim()
            : "";


    const cakeId =
        Number(
            selectedCakeId
        );


    const rating =
        Number(
            selectedRating
        );


    const ratingData = {

        cakeId:
            cakeId,

        rating:
            rating,

        comment:
            comment

    };


    console.log(
        "⭐ SUBMITTING RATING:",
        ratingData
    );


    try {

        const response =
            await fetch(
                `${API_BASE_URL}/api/ratings`,
                {

                    method:
                        "POST",

                    headers: {

                        "Content-Type":
                            "application/json",

                        "Accept":
                            "application/json"

                    },

                    body:
                        JSON.stringify(
                            ratingData
                        )

                }
            );


        const responseText =
            await response.text();


        console.log(
            "Rating HTTP Status:",
            response.status
        );


        console.log(
            "Rating Backend Response:",
            responseText
        );


        if (!response.ok) {

            throw new Error(
                responseText ||
                `HTTP ${response.status}`
            );

        }


        closeRatingModal();


        showToast(
            "⭐ Thank you! Your rating was submitted successfully."
        );


        /*
         * Refresh ratings separately.
         */

        try {

            await loadRatingsForCakes();

            console.log(
                "✅ Ratings refreshed successfully."
            );

        } catch (refreshError) {

            console.warn(
                "⚠️ Rating submitted successfully, but refresh failed:",
                refreshError
            );

        }


        selectedCakeId =
            null;

        selectedRating =
            0;


    } catch (error) {

        console.error(
            "❌ RATING SUBMISSION FAILED:",
            error
        );


        showToast(
            "❌ Failed to submit rating. " +
            "Please make sure Rating Service and API Gateway are running."
        );

    }

}



/* =========================================================
   SEARCH + CATEGORY + PRICE FILTER
========================================================= */

function filterCakes() {

    const searchInput =
        document.getElementById(
            "search-input"
        );


    const categorySelect =
        document.getElementById(
            "category-filter"
        );


    const minPriceInput =
        document.getElementById(
            "min-price"
        );


    const maxPriceInput =
        document.getElementById(
            "max-price"
        );


    const search =
        searchInput
            ? searchInput.value
                .toLowerCase()
                .trim()
            : "";


    const category =
        categorySelect
            ? categorySelect.value
            : "ALL";


    const minPriceText =
        minPriceInput
            ? minPriceInput.value.trim()
            : "";


    const maxPriceText =
        maxPriceInput
            ? maxPriceInput.value.trim()
            : "";


    const minPrice =
        minPriceText === ""
            ? null
            : Number(
                minPriceText
            );


    const maxPrice =
        maxPriceText === ""
            ? null
            : Number(
                maxPriceText
            );


    /* =====================================================
       VALIDATE MINIMUM PRICE
    ===================================================== */

    if (
        minPrice !== null &&
        (
            !Number.isFinite(
                minPrice
            ) ||
            minPrice < 0
        )
    ) {

        showToast(
            "❌ Minimum price must be 0 or greater."
        );

        return;
    }


    /* =====================================================
       VALIDATE MAXIMUM PRICE
    ===================================================== */

    if (
        maxPrice !== null &&
        (
            !Number.isFinite(
                maxPrice
            ) ||
            maxPrice < 0
        )
    ) {

        showToast(
            "❌ Maximum price must be 0 or greater."
        );

        return;
    }


    /* =====================================================
       MIN > MAX
    ===================================================== */

    if (
        minPrice !== null &&
        maxPrice !== null &&
        minPrice > maxPrice
    ) {

        showToast(
            "❌ Minimum price cannot be greater than maximum price."
        );

        return;
    }


    /* =====================================================
       FILTER
    ===================================================== */

    const filtered =
        cakes.filter(
            cake => {

                const name =
                    String(
                        cake.name || ""
                    )
                    .toLowerCase();


                const description =
                    String(
                        cake.description || ""
                    )
                    .toLowerCase();


                const cakeCategory =
                    String(
                        cake.category || ""
                    );


                const cakePrice =
                    Number(
                        cake.price || 0
                    );


                const matchesSearch =
                    name.includes(
                        search
                    ) ||
                    description.includes(
                        search
                    );


                const matchesCategory =
                    category === "ALL" ||
                    cakeCategory
                        .toLowerCase() ===
                    category
                        .toLowerCase();


                const matchesMinPrice =
                    minPrice === null ||
                    cakePrice >= minPrice;


                const matchesMaxPrice =
                    maxPrice === null ||
                    cakePrice <= maxPrice;


                return (
                    matchesSearch &&
                    matchesCategory &&
                    matchesMinPrice &&
                    matchesMaxPrice
                );

            }
        );


    displayCakes(
        filtered
    );

}



/* =========================================================
   CLEAR PRICE FILTER
========================================================= */

function clearPriceFilter() {

    const minPriceInput =
        document.getElementById(
            "min-price"
        );


    const maxPriceInput =
        document.getElementById(
            "max-price"
        );


    if (minPriceInput) {

        minPriceInput.value =
            "";

    }


    if (maxPriceInput) {

        maxPriceInput.value =
            "";

    }


    filterCakes();

}



/* =========================================================
   ADD TO CART
========================================================= */

function addToCart(
    cakeId
) {

    const id =
        Number(
            cakeId
        );


    const cake =
        cakes.find(
            item =>
                Number(
                    item.id
                ) === id
        );


    if (!cake) {

        showToast(
            "❌ Cake not found."
        );

        return;
    }


    if (
        cake.available === false
    ) {

        showToast(
            "❌ This cake is unavailable."
        );

        return;
    }


    const existing =
        cart.find(
            item =>
                Number(
                    item.id
                ) === id
        );


    if (existing) {

        existing.quantity++;

    } else {

        cart.push({

            id:
                id,

            name:
                cake.name,

            price:
                Number(
                    cake.price || 0
                ),

            imageUrl:
                cake.imageUrl ||
                cake.image,

            quantity:
                1

        });

    }


    updateCartUI();


    showToast(
        `${cake.name} added to cart 🛒`
    );

}



/* =========================================================
   CHANGE CART QUANTITY
========================================================= */

function changeQuantity(
    cakeId,
    change
) {

    const id =
        Number(
            cakeId
        );


    const item =
        cart.find(
            cartItem =>
                Number(
                    cartItem.id
                ) === id
        );


    if (!item) {
        return;
    }


    item.quantity +=
        Number(
            change
        );


    if (
        item.quantity <= 0
    ) {

        cart =
            cart.filter(
                cartItem =>
                    Number(
                        cartItem.id
                    ) !== id
            );

    }


    updateCartUI();

}



/* =========================================================
   REMOVE FROM CART
========================================================= */

function removeFromCart(
    cakeId
) {

    const id =
        Number(
            cakeId
        );


    cart =
        cart.filter(
            item =>
                Number(
                    item.id
                ) !== id
        );


    updateCartUI();

}



/* =========================================================
   CALCULATE CART TOTAL
========================================================= */

function calculateCartTotal() {

    return cart.reduce(
        (
            total,
            item
        ) => {

            return total +
                (
                    Number(
                        item.price
                    ) *
                    Number(
                        item.quantity
                    )
                );

        },
        0
    );

}



/* =========================================================
   UPDATE CART UI
========================================================= */

function updateCartUI() {

    const countElement =
        document.getElementById(
            "cart-count"
        );


    const subtitleElement =
        document.getElementById(
            "cart-subtitle"
        );


    const itemsContainer =
        document.getElementById(
            "cart-items"
        );


    const totalElement =
        document.getElementById(
            "cart-total"
        );


    const itemCount =
        cart.reduce(
            (
                sum,
                item
            ) =>
                sum +
                Number(
                    item.quantity
                ),
            0
        );


    const total =
        calculateCartTotal();


    if (countElement) {

        countElement.textContent =
            itemCount;

    }


    if (subtitleElement) {

        subtitleElement.textContent =
            `${itemCount} ${
                itemCount === 1
                    ? "item"
                    : "items"
            }`;

    }


    if (totalElement) {

        totalElement.textContent =
            `₹${total.toFixed(0)}`;

    }


    if (!itemsContainer) {
        return;
    }


    if (
        cart.length === 0
    ) {

        itemsContainer.innerHTML = `

            <div class="empty-cart">

                <div class="empty-icon">
                    🛒
                </div>

                <h3>
                    Your cart is empty
                </h3>

                <p>
                    Add something delicious!
                </p>

            </div>

        `;

        return;
    }


    itemsContainer.innerHTML =
        cart
            .map(
                item => {

                    const itemTotal =
                        Number(
                            item.price
                        ) *
                        Number(
                            item.quantity
                        );


                    return `

                        <div class="cart-item">

                            <img
                                src="${
                                    escapeHtml(
                                        item.imageUrl ||
                                        "https://images.unsplash.com/photo-1578985545062-69928b1d9587?auto=format&fit=crop&w=300&q=80"
                                    )
                                }"
                                alt="${escapeHtml(
                                    item.name
                                )}"
                                class="cart-item-image"
                            >


                            <div class="cart-item-info">

                                <h4>
                                    ${escapeHtml(
                                        item.name
                                    )}
                                </h4>

                                <p>
                                    ₹${Number(
                                        item.price
                                    ).toFixed(0)}
                                </p>


                                <div class="quantity-controls">

                                    <button
                                        type="button"
                                        onclick="changeQuantity(${item.id}, -1)"
                                    >
                                        −
                                    </button>

                                    <span>
                                        ${item.quantity}
                                    </span>

                                    <button
                                        type="button"
                                        onclick="changeQuantity(${item.id}, 1)"
                                    >
                                        +
                                    </button>

                                </div>

                            </div>


                            <div class="cart-item-right">

                                <strong>
                                    ₹${itemTotal.toFixed(0)}
                                </strong>

                                <button
                                    type="button"
                                    class="remove-button"
                                    onclick="removeFromCart(${item.id})"
                                >
                                    🗑️
                                </button>

                            </div>

                        </div>

                    `;

                }
            )
            .join("");

}



/* =========================================================
   OPEN CART
========================================================= */

function openCart() {

    const panel =
        document.getElementById(
            "cart-panel"
        );


    const overlay =
        document.getElementById(
            "cart-overlay"
        );


    if (panel) {

        panel.classList.add(
            "open"
        );

    }


    if (overlay) {

        overlay.classList.add(
            "show"
        );

    }


    document.body.classList.add(
        "no-scroll"
    );

}



/* =========================================================
   CLOSE CART
========================================================= */

function closeCart() {

    const panel =
        document.getElementById(
            "cart-panel"
        );


    const overlay =
        document.getElementById(
            "cart-overlay"
        );


    if (panel) {

        panel.classList.remove(
            "open"
        );

    }


    if (overlay) {

        overlay.classList.remove(
            "show"
        );

    }


    document.body.classList.remove(
        "no-scroll"
    );

}



/* =========================================================
   OPEN CHECKOUT
========================================================= */

function openCheckout() {

    if (
        cart.length === 0
    ) {

        showToast(
            "🛒 Your cart is empty."
        );

        return;
    }


    closeCart();


    const modal =
        document.getElementById(
            "checkout-modal"
        );


    if (modal) {

        modal.classList.add(
            "show"
        );

    }


    document.body.classList.add(
        "no-scroll"
    );


    renderCheckoutItems();

}



/* =========================================================
   CLOSE CHECKOUT
========================================================= */

function closeCheckout() {

    const modal =
        document.getElementById(
            "checkout-modal"
        );


    if (modal) {

        modal.classList.remove(
            "show"
        );

    }


    document.body.classList.remove(
        "no-scroll"
    );

}



/* =========================================================
   CHECKOUT ITEMS
========================================================= */

function renderCheckoutItems() {

    const container =
        document.getElementById(
            "checkout-items"
        );


    const totalElement =
        document.getElementById(
            "checkout-total"
        );


    if (!container) {
        return;
    }


    container.innerHTML =
        cart
            .map(
                item => {

                    const total =
                        Number(
                            item.price
                        ) *
                        Number(
                            item.quantity
                        );


                    return `

                        <div class="checkout-item">

                            <span>
                                ${escapeHtml(
                                    item.name
                                )}

                                × ${item.quantity}
                            </span>

                            <strong>
                                ₹${total.toFixed(0)}
                            </strong>

                        </div>

                    `;

                }
            )
            .join("");


    if (totalElement) {

        totalElement.textContent =
            `₹${calculateCartTotal().toFixed(0)}`;

    }

}



/* =========================================================
   PLACE ORDER
========================================================= */

async function placeOrder(
    event
) {

    event.preventDefault();


    if (
        !cart ||
        cart.length === 0
    ) {

        showToast(
            "🛒 Your cart is empty."
        );

        return;
    }


    const nameInput =
        document.getElementById(
            "customer-name"
        );


    const phoneInput =
        document.getElementById(
            "customer-phone"
        );


    const emailInput =
        document.getElementById(
            "customer-email"
        );


    const addressInput =
        document.getElementById(
            "delivery-address"
        );


    const customerName =
        nameInput
            ? nameInput.value.trim()
            : "";


    const customerPhone =
        phoneInput
            ? phoneInput.value.trim()
            : "";


    const customerEmail =
        emailInput
            ? emailInput.value.trim()
            : "";


    const deliveryAddress =
        addressInput
            ? addressInput.value.trim()
            : "";


    if (!customerName) {

        showToast(
            "Please enter your name."
        );

        return;
    }


    if (
        !/^[0-9]{10}$/.test(
            customerPhone
        )
    ) {

        showToast(
            "Please enter a valid 10-digit phone number."
        );

        return;
    }


    if (
        !customerEmail ||
        !isValidEmail(
            customerEmail
        )
    ) {

        showToast(
            "Please enter a valid email address."
        );

        return;
    }


    if (!deliveryAddress) {

        showToast(
            "Please enter your delivery address."
        );

        return;
    }


    const items =
        cart.map(
            item => ({

                cakeId:
                    Number(
                        item.id
                    ),

                cakeName:
                    item.name,

                price:
                    Number(
                        item.price
                    ),

                quantity:
                    Number(
                        item.quantity
                    )

            })
        );


    const totalAmount =
        Number(
            calculateCartTotal()
        );


    const orderData = {

        customerName:
            customerName,

        customerPhone:
            customerPhone,

        customerEmail:
            customerEmail,

        deliveryAddress:
            deliveryAddress,

        totalAmount:
            totalAmount,

        items:
            items

    };


    console.log(
        "🍰 ORDER DATA:",
        orderData
    );


    const submitButton =
        document.querySelector(
            "#checkout-form button[type='submit']"
        );


    const originalHTML =
        submitButton
            ? submitButton.innerHTML
            : "";


    if (submitButton) {

        submitButton.disabled =
            true;

        submitButton.innerHTML =
            "⏳ Placing Order...";

    }


    try {

        const response =
            await fetch(
                `${API_BASE_URL}/api/orders`,
                {

                    method:
                        "POST",

                    headers: {

                        "Content-Type":
                            "application/json",

                        "Accept":
                            "application/json"

                    },

                    body:
                        JSON.stringify(
                            orderData
                        )

                }
            );


        const responseText =
            await response.text();


        console.log(
            "Order HTTP Status:",
            response.status
        );


        console.log(
            "Backend Response:",
            responseText
        );


        if (!response.ok) {

            throw new Error(
                responseText ||
                `HTTP ${response.status}`
            );

        }


        let createdOrder;


        try {

            createdOrder =
                JSON.parse(
                    responseText
                );

        } catch {

            throw new Error(
                "Server returned invalid JSON."
            );

        }


        console.log(
            "✅ ORDER CREATED:",
            createdOrder
        );


        cart = [];


        updateCartUI();


        closeCheckout();


        const form =
            document.getElementById(
                "checkout-form"
            );


        if (form) {
            form.reset();
        }


        showSuccessModal(
            createdOrder.id,
            customerEmail
        );


        try {

            await loadOrders();

        } catch (ordersError) {

            console.warn(
                "Order created but order history refresh failed:",
                ordersError
            );

        }


        setTimeout(
            () => {

                const orders =
                    document.getElementById(
                        "orders"
                    );


                if (orders) {

                    orders.scrollIntoView({

                        behavior:
                            "smooth",

                        block:
                            "start"

                    });

                }

            },
            800
        );


    } catch (error) {

        console.error(
            "❌ ORDER PLACEMENT FAILED:",
            error
        );


        showToast(
            "❌ Failed to place order. " +
            "Please make sure Order Service/API Gateway is running."
        );


    } finally {

        if (submitButton) {

            submitButton.disabled =
                false;

            submitButton.innerHTML =
                originalHTML;

        }

    }

}



/* =========================================================
   LOAD ORDERS
========================================================= */

async function loadOrders() {

    const container =
        document.getElementById(
            "orders-container"
        );


    if (!container) {
        return;
    }


    try {

        const response =
            await fetch(
                `${API_BASE_URL}/api/orders`
            );


        if (!response.ok) {

            throw new Error(
                `HTTP ${response.status}`
            );

        }


        const data =
            await response.json();


        const orders =
            Array.isArray(data)
                ? data
                : [];


        console.log(
            "📦 Orders loaded:",
            orders
        );


        displayOrders(
            orders
        );


    } catch (error) {

        console.error(
            "❌ Order loading error:",
            error
        );


        container.innerHTML = `

            <div class="empty-orders">

                <div class="empty-icon">
                    📦
                </div>

                <h3>
                    Unable to load orders
                </h3>

                <p>
                    Make sure API Gateway and
                    Order Service are running.
                </p>

            </div>

        `;

    }

}



/* =========================================================
   DISPLAY ORDERS
========================================================= */

function displayOrders(
    orders
) {

    const container =
        document.getElementById(
            "orders-container"
        );


    if (!container) {
        return;
    }


    if (
        !Array.isArray(orders) ||
        orders.length === 0
    ) {

        container.innerHTML = `

            <div class="empty-orders">

                <div class="empty-icon">
                    📦
                </div>

                <h3>
                    No orders yet
                </h3>

                <p>
                    Your placed orders will appear here.
                </p>

            </div>

        `;

        return;
    }


    const sortedOrders =
        [...orders].sort(
            (
                a,
                b
            ) =>
                Number(
                    b.id || 0
                ) -
                Number(
                    a.id || 0
                )
        );


    container.innerHTML =
        sortedOrders
            .map(
                order => {

                    const items =
                        Array.isArray(
                            order.items
                        )
                            ? order.items
                            : [];


                    const date =
                        order.orderDate
                            ? new Date(
                                order.orderDate
                            ).toLocaleString()
                            : "Date unavailable";


                    const status =
                        order.status ||
                        "COMPLETED";


                    const itemsHTML =
                        items.length

                            ? items
                                .map(
                                    item => {

                                        const itemTotal =
                                            Number(
                                                item.price || 0
                                            ) *
                                            Number(
                                                item.quantity || 0
                                            );


                                        return `

                                            <div
                                                class="order-list-item"
                                            >

                                                <div>

                                                    <strong>

                                                        🎂

                                                        ${escapeHtml(
                                                            item.cakeName ||
                                                            "Cake"
                                                        )}

                                                    </strong>

                                                    <span>
                                                        ×
                                                        ${Number(
                                                            item.quantity ||
                                                            0
                                                        )}
                                                    </span>

                                                </div>


                                                <strong>
                                                    ₹${itemTotal.toFixed(0)}
                                                </strong>

                                            </div>

                                        `;

                                    }
                                )
                                .join("")

                            : `

                                <div
                                    class="order-list-item"
                                >

                                    <span>
                                        Item details unavailable
                                    </span>

                                </div>

                            `;


                    return `

                        <article
                            class="order-card"
                        >

                            <div
                                class="order-card-header"
                            >

                                <div>

                                    <h3>
                                        Order #
                                        ${escapeHtml(
                                            order.id
                                        )}
                                    </h3>


                                    <span
                                        class="order-date"
                                    >
                                        ${escapeHtml(
                                            date
                                        )}
                                    </span>

                                </div>


                                <span
                                    class="order-status"
                                >
                                    ${escapeHtml(
                                        status
                                    )}
                                </span>

                            </div>


                            <div
                                class="order-customer"
                            >

                                <div>

                                    <strong>
                                        👤 Customer
                                    </strong>

                                    <span>
                                        ${escapeHtml(
                                            order.customerName ||
                                            "N/A"
                                        )}
                                    </span>

                                </div>


                                <div>

                                    <strong>
                                        📱 Phone
                                    </strong>

                                    <span>
                                        ${escapeHtml(
                                            order.customerPhone ||
                                            "N/A"
                                        )}
                                    </span>

                                </div>


                                <div>

                                    <strong>
                                        ✉️ Email
                                    </strong>

                                    <span>
                                        ${escapeHtml(
                                            order.customerEmail ||
                                            "N/A"
                                        )}
                                    </span>

                                </div>


                                <div>

                                    <strong>
                                        📍 Address
                                    </strong>

                                    <span>
                                        ${escapeHtml(
                                            order.deliveryAddress ||
                                            "N/A"
                                        )}
                                    </span>

                                </div>

                            </div>


                            <div
                                class="order-items"
                            >

                                <h4>
                                    Ordered Items
                                </h4>

                                ${itemsHTML}

                            </div>


                            <div
                                class="order-card-footer"
                            >

                                <strong>
                                    Total
                                </strong>

                                <strong
                                    class="order-total"
                                >
                                    ₹${Number(
                                        order.totalAmount ||
                                        0
                                    ).toFixed(0)}
                                </strong>

                            </div>


                        </article>

                    `;

                }
            )
            .join("");

}



/* =========================================================
   SUCCESS MODAL
========================================================= */

function showSuccessModal(
    orderId,
    email
) {

    const message =
        document.getElementById(
            "success-message"
        );


    const emailElement =
        document.getElementById(
            "success-email"
        );


    const modal =
        document.getElementById(
            "success-modal"
        );


    if (message) {

        message.textContent =
            `Your order #${orderId} has been placed successfully!`;

    }


    if (emailElement) {

        emailElement.textContent =
            email;

    }


    if (modal) {

        modal.classList.add(
            "show"
        );

    }

}



/* =========================================================
   CLOSE SUCCESS MODAL
========================================================= */

function closeSuccessModal() {

    const modal =
        document.getElementById(
            "success-modal"
        );


    if (modal) {

        modal.classList.remove(
            "show"
        );

    }

}



/* =========================================================
   EMAIL VALIDATION
========================================================= */

function isValidEmail(
    email
) {

    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/
        .test(
            email
        );

}



/* =========================================================
   TOAST
========================================================= */

function showToast(
    message
) {

    const toast =
        document.getElementById(
            "toast"
        );


    if (!toast) {

        alert(
            message
        );

        return;
    }


    toast.textContent =
        message;


    toast.classList.add(
        "show"
    );


    clearTimeout(
        window.toastTimer
    );


    window.toastTimer =
        setTimeout(
            () => {

                toast.classList.remove(
                    "show"
                );

            },
            3000
        );

}



/* =========================================================
   ESCAPE JAVASCRIPT STRING
========================================================= */

function escapeJs(
    value
) {

    return String(
        value ?? ""
    )

        .replace(
            /\\/g,
            "\\\\"
        )

        .replace(
            /'/g,
            "\\'"
        )

        .replace(
            /"/g,
            '\\"'
        )

        .replace(
            /\r?\n/g,
            "\\n"
        );

}



/* =========================================================
   SCROLL TO CAKES
========================================================= */

function scrollToCakes() {

    const section =
        document.getElementById(
            "cakes"
        );


    if (section) {

        section.scrollIntoView({

            behavior:
                "smooth"

        });

    }

}



/* =========================================================
   ESCAPE HTML
========================================================= */

function escapeHtml(
    value
) {

    return String(
        value ?? ""
    )

        .replace(
            /&/g,
            "&amp;"
        )

        .replace(
            /</g,
            "&lt;"
        )

        .replace(
            />/g,
            "&gt;"
        )

        .replace(
            /"/g,
            "&quot;"
        )

        .replace(
            /'/g,
            "&#039;"
        );

}



/* =========================================================
   CLOSE MODALS WHEN CLICKING OUTSIDE
========================================================= */

window.addEventListener(
    "click",
    event => {

        const checkoutModal =
            document.getElementById(
                "checkout-modal"
            );


        const successModal =
            document.getElementById(
                "success-modal"
            );


        const ratingModal =
            document.getElementById(
                "rating-modal"
            );


        if (
            event.target ===
            checkoutModal
        ) {

            closeCheckout();

        }


        if (
            event.target ===
            successModal
        ) {

            closeSuccessModal();

        }


        if (
            event.target ===
            ratingModal
        ) {

            closeRatingModal();

        }

    }
);



/* =========================================================
   ESC KEY
========================================================= */

document.addEventListener(
    "keydown",
    event => {

        if (
            event.key ===
            "Escape"
        ) {

            closeCart();

            closeCheckout();

            closeSuccessModal();

            closeRatingModal();

        }

    }
);