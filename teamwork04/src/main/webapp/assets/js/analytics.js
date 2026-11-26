(function () {
  "use strict";

  const timer = document.getElementById("studyTimerElapsed");

  if (timer) {
    const startedAt = new Date(timer.dataset.start);
    const updateTimer = function () {
      const elapsedSeconds = Math.max(0, Math.floor((Date.now() - startedAt.getTime()) / 1000));
      const hours = Math.floor(elapsedSeconds / 3600);
      const minutes = Math.floor((elapsedSeconds % 3600) / 60);
      const seconds = elapsedSeconds % 60;
      timer.textContent = [hours, minutes, seconds]
        .map(function (value) { return String(value).padStart(2, "0"); })
        .join(":");
    };
    updateTimer();
    window.setInterval(updateTimer, 1000);
  }

  const dataElement = document.getElementById("analytics-chart-data");

  if (!dataElement) {
    return;
  }

  let analyticsData;

  try {
    analyticsData = JSON.parse(dataElement.textContent);
  } catch (error) {
    console.error("Study analytics chart data is invalid.", error);
    return;
  }

  if (typeof window.Chart !== "function") {
    document
      .querySelectorAll(".sf-analytics-chart")
      .forEach(function (container) {
        container.textContent =
          "Charts could not be loaded. Refresh the page to try again.";
        container.setAttribute("role", "status");
      });
    return;
  }

  const gridColor = "rgba(105, 119, 139, 0.14)";
  const labelColor =
    getComputedStyle(document.body).getPropertyValue("--sf-muted").trim() ||
    "#718096";

  function createDateLabel(isoDate) {
    const date = new Date(isoDate + "T00:00:00");

    return date.toLocaleDateString(undefined, {
      month: "short",
      day: "numeric",
    });
  }

  function createYAxis(title) {
    return {
      beginAtZero: true,
      ticks: {
        precision: 0,
        color: labelColor,
      },
      title: {
        display: true,
        text: title,
        color: labelColor,
      },
      grid: {
        color: gridColor,
        drawBorder: false,
      },
    };
  }

  function chartOptions(yAxisTitle) {
    return {
      responsive: true,
      maintainAspectRatio: false,
      animation: {
        duration: 350,
      },
      interaction: {
        intersect: false,
        mode: "index",
      },
      plugins: {
        legend: { display: true, position: "bottom" },
        tooltip: {
          callbacks: {
            label: function (context) {
              return context.dataset.yAxisID === "yActions"
                ? context.parsed.y + " activities"
                : context.parsed.y + " minutes";
            },
          },
        },
      },
      scales: {
        x: {
          grid: {
            display: false,
          },
          ticks: {
            color: labelColor,
            maxRotation: 0,
            autoSkip: false,
          },
        },
        y: createYAxis(yAxisTitle),
        yActions: {
          beginAtZero: true,
          position: "right",
          ticks: { precision: 0, color: labelColor },
          title: { display: true, text: "Activities", color: labelColor },
          grid: { drawOnChartArea: false, drawBorder: false },
        },
      },
    };
  }

  const weeklyCanvas = document.getElementById("weeklyStudyChart");

  if (weeklyCanvas) {
    new window.Chart(weeklyCanvas, {
      type: "bar",
      data: {
        labels: analyticsData.weekly.labels,
        datasets: [
          {
            label: "Study time",
            data: analyticsData.weekly.values,
            backgroundColor: "rgba(69, 168, 145, 0.78)",
            hoverBackgroundColor: "rgba(49, 140, 120, 0.95)",
            borderRadius: 8,
            maxBarThickness: 42,
          },
          {
            type: "line",
            label: "Activities",
            data: analyticsData.weekly.actions,
            yAxisID: "yActions",
            borderColor: "#e49345",
            backgroundColor: "#e49345",
            pointRadius: 4,
            pointHoverRadius: 6,
            tension: 0.3,
          },
        ],
      },
      options: chartOptions("Minutes"),
    });
  }

  const monthlyCanvas = document.getElementById("monthlyStudyChart");

  if (monthlyCanvas) {
    const options = chartOptions("Minutes");
    const monthlyLabels = analyticsData.monthly.labels;

    options.scales.x.ticks.callback = function (tickValue, index) {
      const lastIndex = monthlyLabels.length - 1;

      if (index === 0 || index === lastIndex || index % 5 === 0) {
        return createDateLabel(monthlyLabels[index]);
      }

      return "";
    };

    new window.Chart(monthlyCanvas, {
      type: "line",
      data: {
        labels: monthlyLabels,
        datasets: [
          {
            label: "Study time",
            data: analyticsData.monthly.values,
            borderColor: "#318c78",
            backgroundColor: "rgba(69, 168, 145, 0.16)",
            pointBackgroundColor: "#318c78",
            pointBorderColor: "#ffffff",
            pointBorderWidth: 2,
            pointRadius: 3,
            pointHoverRadius: 5,
            borderWidth: 3,
            fill: true,
            tension: 0.35,
          },
          {
            label: "Activities",
            data: analyticsData.monthly.actions,
            yAxisID: "yActions",
            borderColor: "#e49345",
            backgroundColor: "#e49345",
            pointBackgroundColor: "#e49345",
            pointBorderColor: "#ffffff",
            pointBorderWidth: 2,
            pointRadius: 3,
            pointHoverRadius: 5,
            borderWidth: 2,
            tension: 0.3,
          },
        ],
      },
      options: options,
    });
  }
})();
