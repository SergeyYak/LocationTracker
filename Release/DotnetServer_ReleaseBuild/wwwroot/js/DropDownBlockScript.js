"use strict";
{
    let pointerToRight = "▶";
    let pointerToBottom = "▼";
    let hiddenBlock_DisplayStatus = "none";
    let openedBlock_DisplayStatus = "block";

    let pointers = document.querySelectorAll(".pointer");
    pointers.forEach(function (elem) { elem.addEventListener("click", ShowOrHideBlock) });

    function ShowOrHideBlock() {
        let button = this;
        let parent = button.parentElement;
        let parentChild = parent.nextElementSibling;
        if (parentChild.offsetParent === null) {
            button.textContent = pointerToBottom;
            parentChild.style.display = openedBlock_DisplayStatus;
        }
        else {
            button.textContent = pointerToRight;
            parentChild.style.display = hiddenBlock_DisplayStatus;
        }
    }
}