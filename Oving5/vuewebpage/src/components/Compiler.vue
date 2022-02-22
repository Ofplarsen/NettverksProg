<template>
  <textarea v-model="code"></textarea>
  <button @click="compile">Compile and run</button>
  <textarea v-model="result"></textarea>


</template>

<script>
import axios from "axios";

export default {
  // eslint-disable-next-line vue/multi-word-component-names
  name: "Compiler",
  data(){
    return{
      code: "",
      result: "",
    }
  },
  methods:{
    compile: function (){
      this.result = "Please wait..."
      axios.post("http://localhost:8990/compile/",{
        code: this.code
      })
      .then(resp => {
        this.result = resp
      }).catch(err => {
        console.log("Something went wrong")
        console.log(err)
      })
    }
  }
}
</script>

<style scoped>

</style>