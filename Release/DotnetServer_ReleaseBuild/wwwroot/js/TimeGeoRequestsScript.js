"use strict";
{
    let PointerEvents_None_Word = "none";
    let PointerEvents_All_Word = "all";
    let disabledClassString = "disabled";
    let minValue = 0;
    let maxValue = 99;

    let hours = document.querySelectorAll(".hours");
    let minutes = document.querySelectorAll(".minutes");
    let seconds = document.querySelectorAll(".seconds");

    let taskButtons = document.querySelectorAll(".task");
    let button = document.querySelectorAll(".TimeGeoRequests .changeBtn");

    hours.forEach(function (elem) { elem.oninput = OnInput; elem.initialWidth = elem.style.width });
    minutes.forEach(function (elem) { elem.oninput = OnInput; elem.initialWidth = elem.style.width });
    seconds.forEach(function (elem) { elem.oninput = OnInput; elem.initialWidth = elem.style.width });

    button.forEach(function (elem) { elem.addEventListener("click", OnClick) });
    taskButtons.forEach(function (elem) { elem.addEventListener("click", OnClick) });

    function OnInput() {
        let number = Number.parseInt(this.value);
        if (isNaN(number)) this.value = "";
        else this.value = number;

        this.style.width = this.initialWidth;
        this.style.width = (this.scrollWidth - 4) + 'px';

        if (this.value > maxValue) this.value = this.value.slice(0, -1);
        else if (this.value < minValue) this.value = minValue;
    }

    function OnClick() {
        if (this.type == "button") {
            button.forEach(function (elem) { elem.style.display = "none"; });

            document.querySelectorAll(".hours").forEach(function (elem) { elem.style.pointerEvents = PointerEvents_All_Word; });
            document.querySelectorAll(".minutes").forEach(function (elem) { elem.style.pointerEvents = PointerEvents_All_Word; });
            document.querySelectorAll(".seconds").forEach(function (elem) { elem.style.pointerEvents = PointerEvents_All_Word; });

            document.querySelectorAll(".hours").forEach(function (elem) { elem.classList.remove(disabledClassString); });
            document.querySelectorAll(".minutes").forEach(function (elem) { elem.classList.remove(disabledClassString); });
            document.querySelectorAll(".seconds").forEach(function (elem) { elem.classList.remove(disabledClassString); });
        }
        else if (this.type == "submit") {
            button.forEach(function (elem) { elem.style.display = ""; });

            document.querySelectorAll(".hours").forEach(function (elem) { elem.style.pointerEvents = PointerEvents_None_Word; });
            document.querySelectorAll(".minutes").forEach(function (elem) { elem.style.pointerEvents = PointerEvents_None_Word; });
            document.querySelectorAll(".seconds").forEach(function (elem) { elem.style.pointerEvents = PointerEvents_None_Word; });

            document.querySelectorAll(".hours").forEach(function (elem) { elem.classList.add(disabledClassString); });
            document.querySelectorAll(".minutes").forEach(function (elem) { elem.classList.add(disabledClassString); });
            document.querySelectorAll(".seconds").forEach(function (elem) { elem.classList.add(disabledClassString); });
        }
    }
}