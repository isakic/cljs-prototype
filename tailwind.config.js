module.exports = {
  mode: 'jit',
  content: process.env.NODE_ENV === 'production' ? ["./public/app/js/main.js"] : ["./public/app/js/cljs-runtime/*.js"],
  variants: {},
  plugins: [
    require('@tailwindcss/forms'),
  ],
}