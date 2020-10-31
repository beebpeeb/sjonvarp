const path = require("path");

module.exports = {
  entry: path.resolve(__dirname, "target", "public", "js", "index.js"),
  output: {
    path: path.join(__dirname, "resources", "public", "js"),
    filename: "main.js"
  }
};