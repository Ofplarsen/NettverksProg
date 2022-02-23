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
      axios.post("http://localhost:8990/compile",{
        code: this.code
      })
      .then(resp => {
        console.log(resp)
        this.result = resp.data.result
      }).catch(err => {
        console.log("Something went wrong")
        console.log(err)
      })
    }
  }
}
</script>

<style scoped>
textarea{
  width: 30%;
  height: 400px;
}
button{
  height: 100px;
  width: 10%;
}
</style>