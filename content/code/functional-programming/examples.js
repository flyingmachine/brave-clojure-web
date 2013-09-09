// Not modeling everything because
var asymHobbitBodyParts = [
  {name: "head", weight: 3},
  {name: "left-eye", weight: 1},
  {name: "left-ear", weight: 1},
  {name: "mouth", weight: 1},
  {name: "nose", weight: 1},
  {name: "neck", weight: 2}
];

// This is used to return a nested array of symmetrical body parts.
// For example, if the source array of body parts contains
// ["left-elbow", 2]
// then the array returned by this function will also include
// ["right-elbow", 2]
// This allows us to reduce replication, as a left elbow is just as
// likely to be hit as a right elbow.
var symmetrizeBodyParts = function(bodyParts) {
  var finalParts = [];
  var part;
  var sum = 0;
  
  while(bodyParts.length) {
    // MUTATION!!!!
    part = bodyParts.pop();
    finalParts.push(part)
    if (part.name.indexOf("left") > -1) {      
      finalParts.push({name: part.name.replace("left", "right"), weight: part.weight});
    }
  }

  return finalParts;
}

console.log(asymHobbitBodyParts.length)
console.log(symmetrizeBodyParts(asymHobbitBodyParts))
console.log(asymHobbitBodyParts.length)


//////
var rawObjects = getRawObjects();
var analyzedObjects = [];
var l = rawObjects.length;
for(var i=0; i < l; i++){
  // Mutation!
  analyzedObjects.push(analyze(rawObjects[i]));
}


var allPatients = getArkhamPatients();
var analyzedPatients = [];
var l = allPatients.length;
for(var i=0; i < l; i++){
  if(allPatients[i].analyzed){
    analyzedPatients.push(allPatients[i]);
  }
}


////
