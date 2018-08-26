/** 将大写转化为下划线 */
export const convertToUnderscore = key => {
  key.replace(/[A-Z]/g, str => `_${str.toLowerCase()}`);
};

/** 将下划线转化为驼峰 */
export const convertToCamelCase = key => {
  key.replace(/_./g, str => str[1].toUpperCase());
};
