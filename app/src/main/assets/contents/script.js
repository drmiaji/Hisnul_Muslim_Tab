document.addEventListener("DOMContentLoaded", function() {
    const html = document.documentElement;
    const modeBtn = document.getElementById("modeToggle");

    // Function to update button text based on current mode
    const updateMode = () => {
        const isDark = html.classList.contains("dark");
        const showTransliteration = document.body.classList.contains("show-transliteration");

        let text = "";
        if (!isDark && showTransliteration) {
            text = "☀️ উচ্চারণসহ";
        } else if (isDark && showTransliteration) {
            text = "🌙 উচ্চারণসহ (ডার্ক)";
        } else if (isDark && !showTransliteration) {
            text = "🌙 উচ্চারণবিহীন";
        } else {
            text = "☀️ উচ্চারণবিহীন";
        }

        if (modeBtn) {
            modeBtn.textContent = text;
        }
    };

    // Function to apply a specific mode
    const applyMode = (modeIndex) => {
        const modes = [
            { dark: false, translit: true },  // Light with transliteration
            { dark: true, translit: true },   // Dark with transliteration
            { dark: true, translit: false },  // Dark without transliteration
            { dark: false, translit: false }  // Light without transliteration
        ];

        const mode = modes[modeIndex % modes.length]; // Ensure index is within bounds

        // Apply dark/light mode
        if (mode.dark) {
            html.classList.add("dark");
        } else {
            html.classList.remove("dark");
        }

        // Apply transliteration preference
        if (mode.translit) {
            document.body.classList.add("show-transliteration");
        } else {
            document.body.classList.remove("show-transliteration");
        }

        // Save to localStorage if available
        try {
            localStorage.setItem("modeIndex", modeIndex);
        } catch (e) {
            console.error("Failed to save mode to localStorage:", e);
        }

        updateMode();
    };

    // Initialize mode from localStorage or default to 0
    let modeIndex = 0;
    try {
        const savedMode = localStorage.getItem("modeIndex");
        if (savedMode !== null) {
            modeIndex = parseInt(savedMode) || 0;
        }
    } catch (e) {
        console.error("Failed to read mode from localStorage:", e);
    }

    // Apply initial mode
    applyMode(modeIndex);

    // Set up mode toggle button click handler
    if (modeBtn) {
        modeBtn.addEventListener("click", () => {
            modeIndex = (modeIndex + 1) % 4;
            applyMode(modeIndex);
        });
    }

    // Set up transliteration display
    const translits = document.querySelectorAll(".transliteration");
    const updateTransliterationDisplay = () => {
        const show = document.body.classList.contains("show-transliteration");
        translits.forEach(el => {
            if (el) {
                el.style.display = show ? "block" : "none";
            }
        });
    };

    // Initial update
    updateTransliterationDisplay();

    // Observe body class changes for transliteration
    const observer = new MutationObserver(() => {
        updateTransliterationDisplay();
        updateMode();
    });

    if (document.body) {
        observer.observe(document.body, {
            attributes: true,
            attributeFilter: ["class"]
        });
    }

    // Scroll-to-top button functionality
    const goTopBtn = document.getElementById("goTopBtn");
    if (goTopBtn) {
        window.addEventListener("scroll", function() {
            const scrollTop = document.documentElement.scrollTop || document.body.scrollTop;
            goTopBtn.style.display = scrollTop > 200 ? "block" : "none";
        });

        goTopBtn.addEventListener("click", function() {
            window.scrollTo({
                top: 0,
                behavior: "smooth"
            });
        });

        goTopBtn.style.display = "none";
    }
});