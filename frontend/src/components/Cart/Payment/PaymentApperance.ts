export const appearance: {
  theme: "flat" | "stripe" | "night" | "none" | undefined;
  variables: any;
  rules: any;
} = {
  theme: "flat",
  variables: {
    fontWeightNormal: "500",
    borderRadius: "2px",
    colorBackground: "white",
    colorPrimary: "#7E57C2",
    colorPrimaryText: "white",
    spacingGridRow: "15px",
  },
  rules: {
    ".Label": {
      marginBottom: "6px",
    },
    ".Tab, .Input, .Block": {
      boxShadow: "0px 3px 10px rgba(18, 42, 66, 0.08)",
      padding: "12px",
    },
  },
};
